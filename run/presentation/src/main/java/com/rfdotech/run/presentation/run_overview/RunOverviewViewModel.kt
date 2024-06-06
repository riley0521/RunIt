package com.rfdotech.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.Geolocator
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.printAndThrowCancellationException
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.run.RunWithAddress
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.presentation.ui.auth.GoogleAuthUiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val geolocator: Geolocator,
    private val dispatcherProvider: DispatcherProvider,
    private val syncRunScheduler: SyncRunScheduler,
    private val userStorage: UserStorage,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    private val eventChannel = Channel<RunOverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    var listenToWorkInfoJob: Job? = null

    init {
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(SyncType.FetchRuns())
        }

        runRepository
            .getAllLocal()
            .onEach { runs ->
                state = state.copy(isGettingRuns = true)
                val runsWithAddress = getAddressFromRuns(runs)

                state = state.copy(runs = runsWithAddress, isGettingRuns = false)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            // We should sync pending runs FIRST to make sure that the API is up to date with what happen in offline.
            runRepository.syncPendingRuns()
            runRepository.fetchFromRemote()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnSignOutClick -> signOut()
            is RunOverviewAction.DeleteRunById -> {
                viewModelScope.launch {
                    runRepository.deleteById(action.id)
                }
            }
            RunOverviewAction.OnDeleteAccountClick -> {
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
                    googleAuthUiClient.deleteAccount()
                    userStorage.set(null)

                    eventChannel.send(RunOverviewEvent.DeleteAccountSuccessful)
                }
            }

            else -> Unit
        }
    }

    private suspend fun getAddressFromRuns(
        runs: List<Run>
    ): List<RunWithAddress> = withContext(dispatcherProvider.io) {
        val addressJob = runs.map { run ->
            async {
                return@async with(run.location) {
                    val addresses = try {
                        geolocator.getAddressesFromCoordinates(latitude, longitude)
                    } catch (e: Exception) {
                        e.printAndThrowCancellationException()
                        emptyList()
                    }

                    RunWithAddress(
                        run = run,
                        address = addresses.firstOrNull()
                    )
                }
            }
        }

        addressJob.awaitAll()
    }

    private fun signOut() = applicationScope.launch {
        syncRunScheduler.cancelAllSyncs()
        runRepository.deleteAllFromLocal()
        userStorage.set(null) // Remove the session from sharedPrefs
        googleAuthUiClient.signOut()
    }
}