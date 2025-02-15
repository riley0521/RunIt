package com.rfdotech.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rfdotech.core.database.converter.ZonedDateTimeConverter
import com.rfdotech.core.database.dao.AnalyticsDao
import com.rfdotech.core.database.dao.RunDao
import com.rfdotech.core.database.dao.RunPendingSyncDao
import com.rfdotech.core.database.entity.DeletedRunSyncEntity
import com.rfdotech.core.database.entity.RunEntity
import com.rfdotech.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [RunEntity::class, RunPendingSyncEntity::class, DeletedRunSyncEntity::class],
    version = 1
)
@TypeConverters(
    value = [ZonedDateTimeConverter::class]
)
abstract class RunDatabase: RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val analyticsDao: AnalyticsDao
}