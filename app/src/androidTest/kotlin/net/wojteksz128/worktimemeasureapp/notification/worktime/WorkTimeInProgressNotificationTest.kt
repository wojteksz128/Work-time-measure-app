package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.FakeDateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import net.wojteksz128.worktimemeasureapp.util.fixtures.TestFixtures
import net.wojteksz128.worktimemeasureapp.util.fixtures.aComeEvent
import net.wojteksz128.worktimemeasureapp.util.fixtures.aWorkTimeBalance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
        private val WORK_DAY_DATE: LocalDate = TestFixtures.DEFAULT_DATE
    }

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun buildNotification(
        balanceAssembly: (ZonedDateTime) -> WorkTimeBalance = { currentTime ->
            aWorkTimeBalance(
                currentTime
            )
        },
        mockCurrentTime: ZonedDateTime? = null,
    ): Notification {
        mockCurrentTime?.let { (dateTimeProvider as FakeDateTimeProvider).setTime(it) }
        return WorkTimeInProgressNotification(
            context,
            WORK_DAY_DATE,
            balanceAssembly(dateTimeProvider.currentTime),
            dateTimeUtils
        ).build()
    }

    @Test
    fun testStandardAndBalancedTime_SingleEvent() {
        val notification = buildNotification(
            balanceAssembly = { currentTime ->
                aWorkTimeBalance(currentTime) {
                    monthlyBalance = Duration.ofHours(1)
                }
            }
        )

        val contentText = notification.extras.getCharSequence("android.text").toString()
        val title = notification.extras.getCharSequence("android.title").toString()

        assertTrue(contentText.contains("16:00"))
        assertTrue(contentText.contains("15:00"))
        assertEquals(context.getString(R.string.notification_work_in_progress_title), title)
        assertTrue(notification.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertEquals(1, notification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_stop_work),
            notification.actions[0].title
        )
    }

    @Test
    fun testStandardAndBalancedTime_TwoEvents() {
        val notification = buildNotification(
            balanceAssembly = { currentTime ->
                aWorkTimeBalance(currentTime) {
                    todayWorkTime = Duration.ofHours(4)
                    monthlyBalance = Duration.ofHours(1)
                }
            },
            mockCurrentTime = ZonedDateTime.parse("2024-01-15T13:00:00Z"),
        )

        val contentText = notification.extras.getCharSequence("android.text").toString()
        assertTrue(contentText.contains("17:00"))
        assertTrue(contentText.contains("16:00"))
    }

    @Test
    fun testProgressBarIsUpdated() {
        var notification = buildNotification(
            balanceAssembly = { currentTime ->
                aWorkTimeBalance(currentTime) {
                    todayWorkTime = Duration.ofHours(6)
                    monthlyBalance = Duration.ofHours(1)
                }
            },
            mockCurrentTime = ZonedDateTime.parse("2024-01-15T14:00:00Z"),
        )
        assertEquals(7 * 3600, notification.extras.getInt("android.progressMax"))
        assertEquals(6 * 3600, notification.extras.getInt("android.progress"))

        notification = buildNotification(
            balanceAssembly = { currentTime ->
                aWorkTimeBalance(currentTime) {
                    todayWorkTime = Duration.ofHours(6).plusMinutes(30)
                    monthlyBalance = Duration.ofHours(1)
                }
            },
            mockCurrentTime = ZonedDateTime.parse("2024-01-15T14:30:00Z"),
        )
        assertEquals(7 * 3600, notification.extras.getInt("android.progressMax"))
        assertEquals((6.5 * 3600).toInt(), notification.extras.getInt("android.progress"))
    }

    @Test
    fun testLongWorkDay_TimeFormatChangesToLong() {
        val startTime = ZonedDateTime.parse("2024-01-15T20:00:00Z")
        val event = aComeEvent {
            startDate = startTime
            inProgress()
        }
        val notification = buildNotification(
            mockCurrentTime = startTime,
        )

        val contentText = notification.extras.getCharSequence("android.text").toString()
        val expectedStandardEndTimeStr = dateTimeUtils.formatDate(
            context.getString(R.string.notification_time_long_format),
            event.startDate.plusHours(8)
        )
        assertTrue(contentText.contains(expectedStandardEndTimeStr))
    }

    @Test
    fun testProgressBar_FallbackToTodayWorkTime() {
        val notification = buildNotification(
            balanceAssembly = { currentTime ->
                aWorkTimeBalance(currentTime) {
                    todayWorkTime = Duration.ofHours(8)
                }
            },
            mockCurrentTime = ZonedDateTime.parse("2024-01-15T18:00:00Z"),
        )
        val progressBar = notification.extras.getInt("android.progressMax")
        val progress = notification.extras.getInt("android.progress")
        assertEquals(Duration.ofHours(8).seconds.toInt(), progressBar)
        assertEquals(Duration.ofHours(8).seconds.toInt(), progress)
    }
}
