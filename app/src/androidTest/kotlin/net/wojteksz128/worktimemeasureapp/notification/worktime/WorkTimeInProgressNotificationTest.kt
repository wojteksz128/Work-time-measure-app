package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.whenever
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WorkTimeInProgressNotificationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    private lateinit var context: Context

    private val workDayDate = LocalDate.of(2024, 1, 1)

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testStandardAndBalancedTime_SingleEvent() {
        val workDayId = 1L
        val startTime = ZonedDateTime.parse("2024-01-01T08:00:00Z")
        val event = ComeEvent(id = 1L, startDate = startTime, endDate = null, workDayId = workDayId)
        val workDay =
            WorkDay(date = workDayDate).copy(id = workDayId, events = mutableListOf(event))

        val workTimeBalance = WorkTimeBalance(
            todayWorkTime = Duration.ZERO, // just started
            requiredToday = Duration.ofHours(8),
            monthlyBalance = Duration.ofHours(1)
        )

        val notification = WorkTimeInProgressNotification(
            context,
            workDay,
            workTimeBalance,
            dateTimeUtils,
            dateTimeProvider
        )
        val builtNotification = notification.build()

        val contentText = builtNotification.extras.getCharSequence("android.text").toString()

        // Standard End: 8:00 + 8h = 16:00
        // Balanced End: 16:00 - 1h = 15:00
        assertTrue(contentText.contains("16:00"))
        assertTrue(contentText.contains("15:00"))
    }

    @Test
    fun testStandardAndBalancedTime_TwoEvents() {
        val workDayId = 1L
        val startTime1 = ZonedDateTime.parse("2024-01-01T08:00:00Z")
        val endTime1 = ZonedDateTime.parse("2024-01-01T12:00:00Z") // 4 hours worked
        val startTime2 = ZonedDateTime.parse("2024-01-01T13:00:00Z")

        val event1 =
            ComeEvent(id = 1L, startDate = startTime1, endDate = endTime1, workDayId = workDayId)
        val event2 =
            ComeEvent(id = 2L, startDate = startTime2, endDate = null, workDayId = workDayId)
        val workDay =
            WorkDay(date = workDayDate).copy(id = workDayId, events = mutableListOf(event1, event2))

        val workTimeBalance = WorkTimeBalance(
            todayWorkTime = Duration.ofHours(4), // 4 hours from first event
            requiredToday = Duration.ofHours(8),
            monthlyBalance = Duration.ofHours(1)
        )

        val notification = WorkTimeInProgressNotification(
            context,
            workDay,
            workTimeBalance,
            dateTimeUtils,
            dateTimeProvider
        )
        val builtNotification = notification.build()

        val contentText = builtNotification.extras.getCharSequence("android.text").toString()

        // Remaining: 8h - 4h = 4h
        // Standard End: 13:00 + 4h = 17:00
        // Balanced End: 17:00 - 1h = 16:00
        assertTrue(contentText.contains("17:00"))
        assertTrue(contentText.contains("16:00"))
    }

    @Test
    fun testProgressBarIsUpdated() {
        val workDayId = 1L
        val startTime = ZonedDateTime.parse("2024-01-01T08:00:00Z")
        val event = ComeEvent(id = 1L, startDate = startTime, endDate = null, workDayId = workDayId)
        val workDay =
            WorkDay(date = workDayDate).copy(id = workDayId, events = mutableListOf(event))

        val workTimeBalance = WorkTimeBalance(
            todayWorkTime = Duration.ZERO,
            requiredToday = Duration.ofHours(8),
            monthlyBalance = Duration.ofHours(1) // balanced end time is 15:00
        )

        // Time is 14:00. Target end time is 15:00 (balanced)
        whenever(dateTimeProvider.currentTime).thenReturn(ZonedDateTime.parse("2024-01-01T14:00:00Z"))

        var notification = WorkTimeInProgressNotification(
            context,
            workDay,
            workTimeBalance,
            dateTimeUtils,
            dateTimeProvider
        )
        var builtNotification = notification.build()
        var progressBar = builtNotification.extras.getInt("android.progressMax")
        var progress = builtNotification.extras.getInt("android.progress")

        // start time is 8:00, target is 15:00. Total is 7 hours.
        // current time is 14:00, elapsed is 6 hours (since start of work day, not from last event).
        // Today work time is 0 at start, plus duration from 8:00 to 14:00 is 6 hours.
        // But the progress is calculated differently, it's based on time elapsed from last start time vs target end time.
        // Let's re-verify the logic in WorkTimeInProgressNotification.
        // maxProgress = Duration.between(startTime, targetEndTime).seconds
        // currentProgress = Duration.between(startTime, now).seconds
        // startTime = 08:00, targetEndTime = 15:00. max = 7 hours.
        // now = 14:00. current = 6 hours.
        assertEquals(7 * 3600, progressBar)
        assertEquals(6 * 3600, progress)


        // Time is 14:30.
        whenever(dateTimeProvider.currentTime).thenReturn(ZonedDateTime.parse("2024-01-01T14:30:00Z"))

        notification = WorkTimeInProgressNotification(
            context,
            workDay,
            workTimeBalance,
            dateTimeUtils,
            dateTimeProvider
        )
        builtNotification = notification.build()
        progressBar = builtNotification.extras.getInt("android.progressMax")
        progress = builtNotification.extras.getInt("android.progress")

        // elapsed is 6.5 hours
        assertEquals(7 * 3600, progressBar)
        assertEquals((6.5 * 3600).toInt(), progress)
    }
}
