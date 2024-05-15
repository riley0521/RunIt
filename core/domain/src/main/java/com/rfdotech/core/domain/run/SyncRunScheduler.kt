package com.rfdotech.core.domain.run

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

val FETCH_RUNS_INTERVAL = 30.minutes

interface SyncRunScheduler {

    suspend fun scheduleSync(type: SyncType)
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class FetchRuns(val interval: Duration = FETCH_RUNS_INTERVAL): SyncType
        data class DeleteRun(val runId: RunId): SyncType
        class CreateRun(val run: Run, val mapPictureBytes: ByteArray): SyncType
    }
}