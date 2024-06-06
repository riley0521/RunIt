package com.rfdotech.run.data.di

import com.rfdotech.run.data.CreateRunWorker
import com.rfdotech.run.data.DeleteRunWorker
import com.rfdotech.run.data.FetchRunsWorker
import com.rfdotech.run.data.SyncRunWorkerScheduler
import com.rfdotech.core.domain.run.SyncRunScheduler
import com.rfdotech.run.data.AndroidStepObserver
import com.rfdotech.run.data.DeleteRunsFromRemoteDbWorker
import com.rfdotech.run.data.connectivity.PhoneToWatchConnector
import com.rfdotech.run.domain.StepObserver
import com.rfdotech.run.domain.WatchConnector
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunsFromRemoteDbWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
    singleOf(::AndroidStepObserver).bind<StepObserver>()

    singleOf(::PhoneToWatchConnector).bind<WatchConnector>()
}