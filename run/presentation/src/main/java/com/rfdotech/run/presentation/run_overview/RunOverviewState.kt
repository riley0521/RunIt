package com.rfdotech.run.presentation.run_overview

import android.content.Context
import com.rfdotech.core.domain.run.RunWithAddress
import com.rfdotech.core.domain.run.WorkInformation
import com.rfdotech.run.presentation.run_overview.mapper.toRunUi
import com.rfdotech.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunWithAddress> = emptyList(),
    val isGettingRuns: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val workInformation: WorkInformation? = null,
    val hasInternet: Boolean = false
) {
    fun getRunUiList(context: Context): List<RunUi> {
        return runs.map { it.toRunUi(context) }
    }
}
