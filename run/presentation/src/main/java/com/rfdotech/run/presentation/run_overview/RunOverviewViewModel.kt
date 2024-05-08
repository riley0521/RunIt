package com.rfdotech.run.presentation.run_overview

import androidx.lifecycle.ViewModel

class RunOverviewViewModel : ViewModel() {

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnSignOutClick -> TODO()
            else -> Unit
        }
    }
}