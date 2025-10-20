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
import org.mockito.kotlin.whenever
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

    companion object {
        private val NOW = ZonedDateTime.parse("2024-01-01T12:00:00Z")
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        whenever(dateTimeProvider.currentTime).thenReturn(NOW)
    }

    private fun buildNotification(
        standardEndTime: ZonedDateTime? = null,
        balancedEndTime: ZonedDateTime? = null,
    ): Notification {
        return EndOfWorkTimeNotification(
            context,
            dateTimeProvider,
            standardEndTime,
            balancedEndTime
        ).build()
    }

    @Test
    fun build_whenNoEndTimes_showsBasicNotification() {
        val builtNotification = buildNotification()

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
        val standardEndTime = NOW.plusMinutes(30)
        val balancedEndTime = NOW.plusHours(1)

        val builtNotification = buildNotification(standardEndTime, balancedEndTime)

        assertEquals(3, builtNotification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_snooze_to_next),
            builtNotification.actions[1].title
        )
    }

    @Test
    fun build_whenOneEndTimeIsNull_hidesSnoozeToNextAction() {
        val standardEndTime = NOW.plusMinutes(30)

        val builtNotification = buildNotification(standardEndTime)

        assertEquals(2, builtNotification.actions.size)
        val titles = builtNotification.actions.map { it.title.toString() }
        assertFalse(titles.contains(context.getString(R.string.notification_action_snooze_to_next)))
    }

    @Test
    fun build_whenEndTimesInPast_hidesSnoozeToNextAction() {
        val standardEndTime = NOW.minusMinutes(30)
        val balancedEndTime = NOW.minusHours(1)

        val builtNotification = buildNotification(standardEndTime, balancedEndTime)

        assertEquals(2, builtNotification.actions.size)
        val titles = builtNotification.actions.map { it.title.toString() }
        assertFalse(titles.contains(context.getString(R.string.notification_action_snooze_to_next)))
    }

    @Test
    fun build_whenOneEndTimeInFuture_showsSnoozeToNextAction() {
        val standardEndTime = NOW.plusMinutes(30)
        val balancedEndTime = NOW.minusHours(1)

        val builtNotification = buildNotification(standardEndTime, balancedEndTime)

        assertEquals(3, builtNotification.actions.size)
        assertEquals(
            context.getString(R.string.notification_action_snooze_to_next),
            builtNotification.actions[1].title
        )
    }
}
