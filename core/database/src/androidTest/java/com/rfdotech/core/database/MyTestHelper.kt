package com.rfdotech.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before

abstract class MyTestHelper {

    lateinit var db: RunDatabase

    protected lateinit var context: Context

    @Before
    open fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context,
            RunDatabase::class.java
        ).build()
    }

    @After
    open fun tearDown() {
        db.clearAllTables()
        db.close()
    }
}