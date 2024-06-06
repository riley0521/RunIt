package com.rfdotech.run.data.mapper

import androidx.work.WorkInfo
import com.rfdotech.core.domain.run.WorkInformation
import com.rfdotech.core.domain.run.WorkState

fun WorkInfo.toWorkInformation(): WorkInformation {
    return WorkInformation(
        state = state.toWorkState()
    )
}

fun WorkInfo.State.toWorkState(): WorkState {
    return when (this) {
        WorkInfo.State.ENQUEUED -> WorkState.ENQUEUED
        WorkInfo.State.RUNNING -> WorkState.RUNNING
        WorkInfo.State.SUCCEEDED -> WorkState.SUCCEEDED
        WorkInfo.State.FAILED -> WorkState.FAILED
        WorkInfo.State.BLOCKED -> WorkState.BLOCKED
        WorkInfo.State.CANCELLED -> WorkState.CANCELLED
    }
}