package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.Notification
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EndOfWorkTimeNotificationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun build_whenNoEndTimes_showsBasicNotification() {
        // Given
        val notification = EndOfWorkTimeNotification(context, dateTimeProvider)

        // When
        val builtNotification = notification.build()

        // Then
        assertEquals(
            context.getString(R.string.notification_end_of_work_title),
            builtNotification.extras.getString(Notification.EXTRA_TITLE)
        )
        assertEquals(
            context.getString(R.string.notification_end_of_work_text),
            builtNotification.extras.getString(Notification.EXTRA_TEXT)
        )
        assertEquals(2, builtNotification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_stop_work),
            builtNotification.actions[0].title
        )
        assertEquals(
            context.getString(R.string.notification_action_snooze),
            builtNotification.actions[1].title
        )
    }

    @Test
    fun build_whenEndTimesInFuture_showsSnoozeToNextAction() {
        // Given
        val now = ZonedDateTime.now()
        doReturn(now).`when`(dateTimeProvider).currentTime
        val standardEndTime = now.plusMinutes(30)
        val balancedEndTime = now.plusHours(1)

        val notification =
            EndOfWorkTimeNotification(context, dateTimeProvider, standardEndTime, balancedEndTime)

        // When
        val builtNotification = notification.build()

        // Then
        assertEquals(3, builtNotification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_snooze_to_next),
            builtNotification.actions[1].title
        )
    }

    @Test
    fun build_whenOneEndTimeIsNull_hidesSnoozeToNextAction() {
        // Given
        val now = ZonedDateTime.now()
        doReturn(now).`when`(dateTimeProvider).currentTime
        val standardEndTime = now.plusMinutes(30)

        val notification =
            EndOfWorkTimeNotification(context, dateTimeProvider, standardEndTime, null)

        // When
        val builtNotification = notification.build()

        // Then
        assertEquals(2, builtNotification.actions.size)
        val titles = builtNotification.actions.map { it.title.toString() }
        assertFalse(titles.contains(context.getString(R.string.notification_action_snooze_to_next)))
    }

    @Test
    fun build_whenEndTimesInPast_hidesSnoozeToNextAction() {
        // Given
        val now = ZonedDateTime.now()
        doReturn(now).`when`(dateTimeProvider).currentTime
        val standardEndTime = now.minusMinutes(30)
        val balancedEndTime = now.minusHours(1)

        val notification =
            EndOfWorkTimeNotification(context, dateTimeProvider, standardEndTime, balancedEndTime)

        // When
        val builtNotification = notification.build()

        // Then
        assertEquals(2, builtNotification.actions.size)
        val titles = builtNotification.actions.map { it.title.toString() }
        assertFalse(titles.contains(context.getString(R.string.notification_action_snooze_to_next)))
    }

    @Test
    fun build_whenOneEndTimeInFuture_showsSnoozeToNextAction() {
        // Given
        val now = ZonedDateTime.now()
        doReturn(now).`when`(dateTimeProvider).currentTime
        val standardEndTime = now.plusMinutes(30)
        val balancedEndTime = now.minusHours(1)

        val notification =
            EndOfWorkTimeNotification(context, dateTimeProvider, standardEndTime, balancedEndTime)

        // When
        val builtNotification = notification.build()

        // Then
        assertEquals(3, builtNotification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_snooze_to_next),
            builtNotification.actions[1].title
        )
    }
}
