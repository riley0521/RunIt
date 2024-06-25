package com.rfdotech.run.presentation.run_overview

import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.WorkInformation

data class RunOverviewState(
    val runs: List<Run> = emptyList(),
    val isGettingRuns: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val workInformation: WorkInformation? = null,
    val hasInternet: Boolean = false,
    val showRationale: Boolean = false
)
