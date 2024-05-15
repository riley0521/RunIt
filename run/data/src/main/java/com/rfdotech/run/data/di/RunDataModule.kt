package com.rfdotech.run.data.di

import com.rfdotech.run.data.CreateRunWorker
import com.rfdotech.run.data.DeleteRunWorker
import com.rfdotech.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)
}