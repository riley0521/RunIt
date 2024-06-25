package com.rfdotech.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.connectivity.domain.ConnectivityObserver
import com.rfdotech.core.connectivity.domain.ConnectivityObserver.Status
import com.rfdotech.core.domain.Address
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.Geolocator
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.location.Location
import com.rfdotech.core.domain.printAndThrowCancellationException
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.presentation.ui.auth.GoogleAuthUiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val geolocator: Geolocator,
    private val dispatcherProvider: DispatcherProvider,
    private val syncRunScheduler: SyncRunScheduler,
    private val userStorage: UserStorage,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val applicationScope: CoroutineScope,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    private val eventChannel = Channel<RunOverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    private var listenToWorkInfoJob: Job? = null

    init {
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(SyncType.FetchRuns())
        }

        runRepository
            .getAllLocal()
            .onEach { runs ->
                state = state.copy(runs = runs)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            // We should sync pending runs FIRST to make sure that the API is up to date with what happen in offline.
            runRepository.syncPendingRuns()
            runRepository.fetchFromRemote()
        }

        connectivityObserver
            .observe()
            .onEach { status ->
                state = state.copy(hasInternet = status == Status.AVAILABLE)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnSignOutClick -> signOut()
            is RunOverviewAction.SubmitPostNotificationPermissionInfo -> {
                state = state.copy(showRationale = action.shouldShowRationale)
            }

            RunOverviewAction.DismissRationaleDialog -> {
                state = state.copy(showRationale = false)
            }

            is RunOverviewAction.DeleteRunById -> {
                viewModelScope.launch {
                    runRepository.deleteById(action.id)
                }
            }

            RunOverviewAction.OnDeleteAccountClick -> {
                if (!state.hasInternet) {
                    viewModelScope.launch {
                        eventChannel.send(RunOverviewEvent.NoInternet)
                    }
                    return
                }

                state = state.copy(showDeleteAccountDialog = true)
            }

            is RunOverviewAction.DeleteAccount -> {
                state = state.copy(showDeleteAccountDialog = false)
                if (action.agreed) {
                    viewModelScope.launch {
                        // Cancel current workers because we are now focusing on deleting all runs.
                        syncRunScheduler.cancelAllSyncs()
                        syncRunScheduler.scheduleSync(SyncType.DeleteRunsFromRemoteDb)

                        listenToWorkInfoJob = syncRunScheduler
                            .getWorkInformationForDeleteAllRuns()
                            .onEach {
                                state = state.copy(workInformation = it)
                            }
                            .launchIn(viewModelScope)
                    }
                }
            }

            RunOverviewAction.ConfirmDeleteAccount -> {
                listenToWorkInfoJob?.cancel()
                listenToWorkInfoJob = null

                viewModelScope.launch {
                    userStorage.set(null) // Remove the session from sharedPrefs
                    val result = googleAuthUiClient.deleteAccount()
                    if (result is Result.Error && result.error == DataError.Network.RE_AUTHENTICATE) {
                        eventChannel.send(RunOverviewEvent.SignInAgain)
                        return@launch
                    }

                    // We need to sign out the oneTapClient.
                    googleAuthUiClient.signOut()
                    eventChannel.send(RunOverviewEvent.DeleteAccountSuccessful)
                }
            }

            else -> Unit
        }
    }

    /**
     * Since the app is offline-first, we might wait too long (if the user is not connected to internet)
     * before fetching an address based on latitude and longitude,
     * so we will only wait the response for 3 seconds.
     */
    suspend fun getAddressFromLocation(
        location: Location
    ): Address? {
        return try {
            withTimeout(3.seconds) {
                withContext(dispatcherProvider.io) {
                    val addresses = try {
                        geolocator.getAddressesFromCoordinates(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    } catch (e: Exception) {
                        e.printAndThrowCancellationException()
                        emptyList()
                    }

                    addresses.firstOrNull()
                }
            }
        } catch (e: TimeoutCancellationException) {
            e.printAndThrowCancellationException()
            null
        }
    }

    private fun signOut() = applicationScope.launch {
        syncRunScheduler.cancelAllSyncs()
        runRepository.deleteAllFromLocal()
        userStorage.set(null) // Remove the session from sharedPrefs
        googleAuthUiClient.signOut()
    }
}