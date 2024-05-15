package com.rfdotech.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfdotech.core.domain.run.RunRepository
import com.rfdotech.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        runRepository
            .getAllLocal()
            .onEach { runs ->
                state = state.copy(runs = runs.map { it.toRunUi() })
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
            RunOverviewAction.OnSignOutClick -> {}
            is RunOverviewAction.DeleteRunById -> {
                viewModelScope.launch {
                    runRepository.deleteById(action.id)
                }
            }
            else -> Unit
        }
    }
}