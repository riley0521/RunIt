package com.rfdotech.core.test_util.run

import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType

class FakeSyncRunScheduler : SyncRunScheduler {

    var isCreateRunWorkerScheduled = false
        private set

    var isDeleteRunWorkerScheduled = false
        private set

    var isFetchRunsWorkerScheduled = false
        private set

    override suspend fun scheduleSync(type: SyncType) {
        when (type) {
            is SyncType.CreateRun -> {
                isCreateRunWorkerScheduled = true
            }
            is SyncType.DeleteRun -> {
                isDeleteRunWorkerScheduled = true
            }
            is SyncType.FetchRuns -> {
                isFetchRunsWorkerScheduled = true
            }
        }
    }

    override suspend fun cancelAllSyncs() {
        isCreateRunWorkerScheduled = false
        isDeleteRunWorkerScheduled = false
        isFetchRunsWorkerScheduled = false
    }
}