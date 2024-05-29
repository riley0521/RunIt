package com.rfdotech.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.domain.auth.UserStorage
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.presentation.ui.auth.GoogleAuthUiClient
import com.rfdotech.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val userStorage: UserStorage,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

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
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnSignOutClick -> signOut()
            is RunOverviewAction.DeleteRunById -> {
                viewModelScope.launch {
                    runRepository.deleteById(action.id)
                }
            }
            else -> Unit
        }
    }

    private fun signOut() = applicationScope.launch {
        syncRunScheduler.cancelAllSyncs()
        runRepository.deleteAllFromLocal()
        userStorage.set(null) // Remove the session from sharedPrefs
        googleAuthUiClient.signOut()
    }
}