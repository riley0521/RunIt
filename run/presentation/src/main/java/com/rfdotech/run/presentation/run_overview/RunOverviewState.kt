package com.rfdotech.run.presentation.run_overview

import com.rfdotech.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList()
)
