package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.formatToString
import net.wojteksz128.worktimemeasureapp.util.fixtures.TestFixtures
import net.wojteksz128.worktimemeasureapp.util.fixtures.aInProgressState
import net.wojteksz128.worktimemeasureapp.util.fixtures.aNotEndedComeEvent
import net.wojteksz128.worktimemeasureapp.util.fixtures.aWorkDay
import net.wojteksz128.worktimemeasureapp.util.fixtures.aWorkTimeRequirements
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WorkTimeInProgressNotificationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testStandardAndBalancedTime_SingleEvent() {
        val inProgressState = aInProgressState {
            workTimeRequirements = aWorkTimeRequirements {
                monthlyBalance = Duration.ofHours(1)
            }
        }

        val notification = buildNotification(inProgressState)

        val contentText = notification.extras.getCharSequence("android.text").toString()
        val title = notification.extras.getCharSequence("android.title").toString()
        val (standardEndTime, balancedEndTime) = extractEndTimesFromNotificationMessage(contentText)
        val expectedStandardTime = TestFixtures.DEFAULT_START_TIME.toLocalTime().plusHours(8)
        val expectedBalancedTime = expectedStandardTime.minusHours(1)

        assertEquals(expectedStandardTime, standardEndTime)
        assertEquals(expectedBalancedTime, balancedEndTime)
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
        val inProgressState = aInProgressState {
            workTimeRequirements = aWorkTimeRequirements {
                monthlyBalance = Duration.ofHours(1)
            }
            currentTime = TestFixtures.DEFAULT_START_TIME.plusHours(4)
        }

        val notification = buildNotification(inProgressState)

        val contentText = notification.extras.getCharSequence("android.text").toString()
        val (standardEndTime, balancedEndTime) = extractEndTimesFromNotificationMessage(contentText)
        val expectedStandardTime = TestFixtures.DEFAULT_START_TIME.toLocalTime().plusHours(8)
        val expectedBalancedTime = expectedStandardTime.minusHours(1)

        assertEquals(expectedStandardTime, standardEndTime)
        assertEquals(expectedBalancedTime, balancedEndTime)
    }

    @Test
    fun testProgressBarIsUpdated() {
        val inProgressState = aInProgressState {
            workTimeRequirements = aWorkTimeRequirements {
                monthlyBalance = Duration.ofHours(1)
            }
            currentTime = TestFixtures.DEFAULT_START_TIME.plusHours(6)
        }

        var notification = buildNotification(inProgressState)
        assertEquals(7 * 3600, notification.extras.getInt("android.progressMax"))
        assertEquals(6 * 3600, notification.extras.getInt("android.progress"))

        val updatedInProgressState = inProgressState.copy(
            currentTime = TestFixtures.DEFAULT_START_TIME.plusHours(6).plusMinutes(30)
        )
        notification = buildNotification(updatedInProgressState)
        assertEquals(7 * 3600, notification.extras.getInt("android.progressMax"))
        assertEquals((6.5 * 3600).toInt(), notification.extras.getInt("android.progress"))
    }

    @Test
    fun testLongWorkDay_TimeFormatChangesToLong() {
        val startTime = ZonedDateTime.parse("2024-01-15T20:00:00Z")
        val inProgressState = aInProgressState {
            workDay = aWorkDay {
                events(aNotEndedComeEvent { startDate = startTime })
            }
            currentTime = startTime
        }

        val notification = buildNotification(inProgressState)

        val contentText = notification.extras.getCharSequence("android.text").toString()
        val expectedStandardEndTimeStr =
            inProgressState.workDay.events.last().startDate.plusHours(8)
                .formatToString(context.getString(R.string.notification_time_long_format))
        assertTrue(contentText.contains(expectedStandardEndTimeStr))
    }

    @Test
    fun testProgressBar_FallbackToTodayWorkTime() {
        val inProgressState = aInProgressState {
            currentTime = TestFixtures.DEFAULT_END_TIME.plusHours(2)
        }

        val notification = buildNotification(inProgressState)

        val progressBar = notification.extras.getInt("android.progressMax")
        val progress = notification.extras.getInt("android.progress")
        assertEquals(Duration.ofHours(8).seconds.toInt(), progressBar)
        assertEquals(Duration.ofHours(8).seconds.toInt(), progress)
    }

    private fun buildNotification(inProgressState: WorkState.InProgress): Notification {
        return WorkTimeInProgressNotification(context, inProgressState).build()
    }

    private fun extractEndTimesFromNotificationMessage(contentText: String): Pair<LocalTime, LocalTime> {
        val contentTemplate = context.getString(R.string.notification_work_in_progress_text)
            .replace(".", "\\.")
            .replace("%s", "(.+?)")
        val regexPattern = Regex(contentTemplate)

        val matchResult = regexPattern.find(contentText)
            ?: throw IllegalArgumentException(
                """Text not match with pattern:
                Pattern: '${regexPattern}'
                Text:    '${contentText}'""".trim()
            )

        val expectedEnd = LocalTime.parse(matchResult.groupValues[1])
        val balancedEnd = LocalTime.parse(matchResult.groupValues[2])

        return expectedEnd to balancedEnd
    }
}
