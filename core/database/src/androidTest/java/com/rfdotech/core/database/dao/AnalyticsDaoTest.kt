package com.rfdotech.core.database.dao

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.rfdotech.core.database.MyTestHelper
import com.rfdotech.core.database.runEntity
import com.rfdotech.core.domain.ZonedDateTimeHelper
import com.rfdotech.core.domain.run.DistanceAndSpeedCalculator
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

class AnalyticsDaoTest : MyTestHelper() {

    @Test
    fun testAnalytics() = runTest {
        val firstRun = runEntity(distanceMeters = 2500)
        val secondRun = runEntity(distanceMeters = 2500, maxSpeedKmh = 11.5)

        db.runDao.upsert(firstRun)
        db.runDao.upsert(secondRun)

        val totalDistanceInMeters = db.analyticsDao.getTotalDistance()
        assertThat(totalDistanceInMeters).isEqualTo(5000)

        val totalTimeRunInMillis = db.analyticsDao.getTotalTimeRun()
        val expectedTotalTimeRun = 60.minutes.inWholeMilliseconds
        assertThat(totalTimeRunInMillis).isEqualTo(expectedTotalTimeRun)

        val maxRunSpeed = db.analyticsDao.getMaxRunSpeed()
        assertThat(maxRunSpeed).isEqualTo(15.0)

        val avgDistancePerRun = db.analyticsDao.getAvgDistancePerRun()
        assertThat(avgDistancePerRun).isEqualTo(2500.0)

        val avgPacePerRun = db.analyticsDao.getAvgPacePerRun()
        val firstRunPace = (firstRun.durationMillis / 60000.0) / DistanceAndSpeedCalculator.getKmFromMeters(firstRun.distanceMeters)
        val secondRunPace = (secondRun.durationMillis / 60000.0) / DistanceAndSpeedCalculator.getKmFromMeters(secondRun.distanceMeters)
        val expectedAvgPace = (firstRunPace + secondRunPace) / 2
        assertThat(avgPacePerRun).isEqualTo(expectedAvgPace)
    }

    @Test
    fun testGetAllRunsThisMonth_InsertRunFromThisMonth_MustBeIncluded() = runTest {
        val firstRun = runEntity(distanceMeters = 2500)

        db.runDao.upsert(firstRun)

        val runs = db.analyticsDao.getAllRunsThisMonth()
        println(runs)

        assertThat(runs).isNotEmpty()
    }

    @Test
    fun testGetAllRunsThisMonth_InsertRunFromLastMonth_MustNotBeIncluded() = runTest {
        val firstRun = runEntity(distanceMeters = 2500, dateTimeUtc = ZonedDateTime.of(LocalDate.of(2024, 4, 1).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE))

        db.runDao.upsert(firstRun)

        val runs = db.analyticsDao.getAllRunsThisMonth()
        println(runs)

        assertThat(runs).isEmpty()
    }

    @Test
    fun testGetAllRunsBetweenDates_InsertRunFromThisMonth_MustBeIncluded() = runTest {
        val firstRun = runEntity(distanceMeters = 2500, dateTimeUtc = ZonedDateTime.of(LocalDate.of(2024, 5, 20).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE))

        val startDate = ZonedDateTime.of(LocalDate.of(2024, 5, 1).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE)
        val endDate = ZonedDateTime.of(LocalDate.of(2024, 5, 30).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE)

        db.runDao.upsert(firstRun)

        val runs = db.analyticsDao.getAllRunsBetweenDates(startDate, endDate)
        println(runs)

        assertThat(runs).isNotEmpty()
    }

    @Test
    fun testGetAllRunsBetweenDates_InsertRunFromLastMonth_MustNotBeIncluded() = runTest {
        val firstRun = runEntity(distanceMeters = 2500, dateTimeUtc = ZonedDateTime.of(LocalDate.of(2024, 4, 20).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE))

        val startDate = ZonedDateTime.of(LocalDate.of(2024, 5, 1).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE)
        val endDate = ZonedDateTime.of(LocalDate.of(2024, 5, 30).atTime(0, 0), ZonedDateTimeHelper.UTC_ZONE)

        db.runDao.upsert(firstRun)

        val runs = db.analyticsDao.getAllRunsBetweenDates(startDate, endDate)
        println(runs)

        assertThat(runs).isEmpty()
    }
}