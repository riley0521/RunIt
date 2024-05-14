package com.rfdotech.core.database.di

import androidx.room.Room
import com.rfdotech.core.database.RoomLocalRunDataSource
import com.rfdotech.core.database.RunDatabase
import com.rfdotech.core.database.dao.RunDao
import com.rfdotech.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDatabaseModule = module {
    single<RunDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run.db"
        ).build()
    }
    single<RunDao> {
        get<RunDatabase>().runDao
    }
    singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}