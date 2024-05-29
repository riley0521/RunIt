package com.rfdotech.run.presentation.run_overview

import android.content.Context
import com.rfdotech.core.domain.run.Run
import com.rfdotech.run.presentation.run_overview.mapper.toRunUi
import com.rfdotech.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<Run> = emptyList()
) {
    fun getRunUiList(context: Context): List<RunUi> {
        return runs.map { it.toRunUi(context) }
    }
}
