package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver.Companion.BALANCED_END_TIME
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver.Companion.NEXT_NOTIFICATION_TIME
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver.Companion.STANDARD_END_TIME
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WorkTimeNotificationActionReceiverTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var comeEventUtils: ComeEventUtils

    @Inject
    lateinit var workDayFlow: MutableStateFlow<WorkDay?>

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    // WorkTimeNotificationService will be mocked to verify interactions
    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    private lateinit var context: Context
    private lateinit var receiver: WorkTimeNotificationActionReceiver

    companion object {
        private val START_TIME: ZonedDateTime = ZonedDateTime.parse("2024-01-01T08:00:00Z")
        private val NOW: ZonedDateTime = ZonedDateTime.parse("2024-01-01T16:00:00Z")
    }

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext

        // Manually create receiver and inject dependencies
        receiver = WorkTimeNotificationActionReceiver().apply {
            this.comeEventUtils = this@WorkTimeNotificationActionReceiverTest.comeEventUtils
            this.dateTimeProvider = this@WorkTimeNotificationActionReceiverTest.dateTimeProvider
            this.notificationService =
                this@WorkTimeNotificationActionReceiverTest.notificationService
        }

        // Mock current time for snooze tests
        whenever(dateTimeProvider.currentTime).thenReturn(NOW)
    }

    private fun sendActionIntent(action: String, intentConfig: (Intent.() -> Unit)? = null) {
        val intent = Intent(context, WorkTimeNotificationActionReceiver::class.java).apply {
            this.action = action
            intentConfig?.invoke(this)
        }
        receiver.onReceive(context, intent)
    }

    @Test
    fun stop_work_action_updates_end_time_for_last_event(): Unit = runBlocking {
        // Arrange: Start work first
        whenever(dateTimeProvider.currentTime).thenReturn(START_TIME)
        comeEventUtils.registerNewEvent() // This starts the work
        assertNotNull("WorkDay should be created", workDayFlow.value)

        // Arrange: Prepare for stop action
        whenever(dateTimeProvider.currentTime).thenReturn(NOW)

        // Act
        sendActionIntent(WorkTimeNotificationService.STOP_WORK_ACTION)

        // Assert: wait for the flow to emit the updated state
        val updatedWorkDay = withTimeoutOrNull(5000) {
            workDayFlow.filterNotNull()
                .first { it.events.isNotEmpty() && it.events.first().isEnded }
        }

        assertNotNull("Test timed out waiting for work day update", updatedWorkDay)
        val finalEvent = updatedWorkDay!!.events.first()
        assertEquals(NOW, finalEvent.endDate)
    }

    @Test
    fun snooze_action_schedules_next_notification_in_10_minutes() {
        // Act
        sendActionIntent(WorkTimeNotificationService.SNOOZE_ACTION)

        // Assert
        val expectedNextReminder = NOW.plusMinutes(10)
        verify(notificationService).scheduleEndOfWorkNotification(expectedNextReminder)
        verify(notificationService).hideEndOfWorkNotification()
    }

    @Test
    fun snooze_to_next_action_schedules_notification_at_correct_time() {
        // Arrange
        val standardEndTime = NOW.plusHours(1)
        val balancedEndTime = NOW.plusHours(2)
        val nextTime = standardEndTime // The nearest future time

        // Act
        sendActionIntent(WorkTimeNotificationService.SNOOZE_TO_NEXT_ACTION) {
            putExtra(NEXT_NOTIFICATION_TIME, nextTime.toString())
            putExtra(STANDARD_END_TIME, standardEndTime.toString())
            putExtra(BALANCED_END_TIME, balancedEndTime.toString())
        }

        // Assert
        verify(notificationService).scheduleEndOfWorkNotification(
            eq(nextTime),
            eq(standardEndTime),
            eq(balancedEndTime)
        )
        verify(notificationService).hideEndOfWorkNotification()
    }

    @Test
    fun snooze_to_next_action_does_nothing_when_intent_extra_is_missing() {
        // Act
        sendActionIntent(WorkTimeNotificationService.SNOOZE_TO_NEXT_ACTION)
        // Intent is missing NEXT_NOTIFICATION_TIME extra

        // Assert
        verify(notificationService, never()).scheduleEndOfWorkNotification(any(), any(), any())
        verify(notificationService, never()).hideEndOfWorkNotification()
    }
}
