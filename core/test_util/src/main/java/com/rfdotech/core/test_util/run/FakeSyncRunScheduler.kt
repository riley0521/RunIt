package com.rfdotech.core.test_util.run

import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler.SyncType
import com.rfdotech.core.domain.run.WorkInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSyncRunScheduler : SyncRunScheduler {

    var isCreateRunWorkerScheduled = false
        private set

    var isDeleteRunWorkerScheduled = false
        private set

    var isFetchRunsWorkerScheduled = false
        private set

    var isDeleteAllRunsWorkerScheduled = false
        private set

    var workInformation: WorkInformation? = null

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

            SyncType.DeleteRunsFromRemoteDb -> {
                isDeleteRunWorkerScheduled = true
            }
        }
    }

    override fun getWorkInformationForDeleteAllRuns(): Flow<WorkInformation?> {
        return flowOf(workInformation)
    }

    override suspend fun cancelAllSyncs() {
        isCreateRunWorkerScheduled = false
        isDeleteRunWorkerScheduled = false
        isFetchRunsWorkerScheduled = false
    }
}