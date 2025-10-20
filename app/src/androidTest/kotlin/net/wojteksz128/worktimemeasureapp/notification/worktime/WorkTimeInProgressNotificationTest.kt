package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
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

    companion object {
        private const val WORK_DAY_ID = 1L
        private val WORK_DAY_DATE = LocalDate.of(2024, 1, 1)
        private val START_TIME_08_00 = ZonedDateTime.parse("2024-01-01T08:00:00Z")
    }

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun createAndBuildNotification(
        events: List<ComeEvent>,
        todayWorkTime: Duration = Duration.ZERO,
        requiredToday: Duration = Duration.ofHours(8),
        monthlyBalance: Duration = Duration.ZERO,
        mockCurrentTime: ZonedDateTime? = null,
    ): Notification {
        val workDay =
            WorkDay(date = WORK_DAY_DATE).copy(id = WORK_DAY_ID, events = events.toMutableList())
        val workTimeBalance = WorkTimeBalance(
            todayWorkTime = todayWorkTime,
            requiredToday = requiredToday,
            monthlyBalance = monthlyBalance
        )

        mockCurrentTime?.let {
            whenever(dateTimeProvider.currentTime).thenReturn(it)
        }

        return WorkTimeInProgressNotification(
            context,
            workDay,
            workTimeBalance,
            dateTimeUtils,
            dateTimeProvider
        ).build()
    }

    @Test
    fun testStandardAndBalancedTime_SingleEvent() {
        val event = ComeEvent(
            id = 1L,
            startDate = START_TIME_08_00,
            endDate = null,
            workDayId = WORK_DAY_ID
        )
        val builtNotification = createAndBuildNotification(
            events = listOf(event),
            monthlyBalance = Duration.ofHours(1)
        )

        val contentText = builtNotification.extras.getCharSequence("android.text").toString()
        val title = builtNotification.extras.getCharSequence("android.title").toString()

        assertTrue(contentText.contains("16:00"))
        assertTrue(contentText.contains("15:00"))
        assertEquals(context.getString(R.string.notification_work_in_progress_title), title)
        assertTrue(builtNotification.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertEquals(1, builtNotification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_stop_work),
            builtNotification.actions[0].title
        )
    }

    @Test
    fun testStandardAndBalancedTime_TwoEvents() {
        val event1 = ComeEvent(
            id = 1L,
            startDate = START_TIME_08_00,
            endDate = ZonedDateTime.parse("2024-01-01T12:00:00Z"),
            workDayId = WORK_DAY_ID
        )
        val event2 = ComeEvent(
            id = 2L,
            startDate = ZonedDateTime.parse("2024-01-01T13:00:00Z"),
            endDate = null,
            workDayId = WORK_DAY_ID
        )

        val builtNotification = createAndBuildNotification(
            events = listOf(event1, event2),
            todayWorkTime = Duration.ofHours(4),
            monthlyBalance = Duration.ofHours(1)
        )

        val contentText = builtNotification.extras.getCharSequence("android.text").toString()
        assertTrue(contentText.contains("17:00"))
        assertTrue(contentText.contains("16:00"))
    }

    @Test
    fun testProgressBarIsUpdated() {
        val event = ComeEvent(
            id = 1L,
            startDate = START_TIME_08_00,
            endDate = null,
            workDayId = WORK_DAY_ID
        )

        var builtNotification = createAndBuildNotification(
            events = listOf(event),
            monthlyBalance = Duration.ofHours(1),
            mockCurrentTime = ZonedDateTime.parse("2024-01-01T14:00:00Z")
        )
        var progressBar = builtNotification.extras.getInt("android.progressMax")
        var progress = builtNotification.extras.getInt("android.progress")
        assertEquals(7 * 3600, progressBar)
        assertEquals(6 * 3600, progress)

        builtNotification = createAndBuildNotification(
            events = listOf(event),
            monthlyBalance = Duration.ofHours(1),
            mockCurrentTime = ZonedDateTime.parse("2024-01-01T14:30:00Z")
        )
        progressBar = builtNotification.extras.getInt("android.progressMax")
        progress = builtNotification.extras.getInt("android.progress")
        assertEquals(7 * 3600, progressBar)
        assertEquals((6.5 * 3600).toInt(), progress)
    }

    @Test
    fun testLongWorkDay_TimeFormatChangesToLong() {
        val event = ComeEvent(
            id = 1L,
            startDate = ZonedDateTime.parse("2024-01-01T20:00:00Z"),
            endDate = null,
            workDayId = WORK_DAY_ID
        )
        val builtNotification = createAndBuildNotification(events = listOf(event))

        val contentText = builtNotification.extras.getCharSequence("android.text").toString()
        val expectedStandardEndTimeStr = dateTimeUtils.formatDate(
            context.getString(R.string.notification_time_long_format),
            event.startDate.plusHours(8)
        )
        assertTrue(contentText.contains(expectedStandardEndTimeStr))
    }

    @Test
    fun testProgressBar_FallbackToTodayWorkTime() {
        val event = ComeEvent(
            id = 1L,
            startDate = START_TIME_08_00,
            endDate = null,
            workDayId = WORK_DAY_ID
        )
        val builtNotification = createAndBuildNotification(
            events = listOf(event),
            todayWorkTime = Duration.ofHours(8),
            mockCurrentTime = ZonedDateTime.parse("2024-01-01T18:00:00Z")
        )
        val progressBar = builtNotification.extras.getInt("android.progressMax")
        val progress = builtNotification.extras.getInt("android.progress")
        assertEquals(Duration.ofHours(8).seconds.toInt(), progressBar)
        assertEquals(Duration.ofHours(8).seconds.toInt(), progress)
    }
}
