package com.rfdotech.run.data.di

import com.rfdotech.run.data.CreateRunWorker
import com.rfdotech.run.data.DeleteRunWorker
import com.rfdotech.run.data.FetchRunsWorker
import com.rfdotech.run.data.SyncRunWorkerScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}