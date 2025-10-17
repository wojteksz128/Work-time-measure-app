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
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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

    @Inject
    lateinit var notificationService: WorkTimeNotificationService

    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun stop_work_action_updates_end_time_for_last_event(): Unit = runBlocking {
        // Arrange: Start work first
        val startTime = ZonedDateTime.parse("2024-01-01T08:00:00Z")
        whenever(dateTimeProvider.currentTime).thenReturn(startTime)
        comeEventUtils.registerNewEvent() // This starts the work
        assertNotNull("WorkDay should be created", workDayFlow.value)
        assertNull(
            "End date should be null after starting work",
            workDayFlow.value!!.events.first().endDate
        )

        // Arrange: Prepare for stop action
        val stopTime = ZonedDateTime.parse("2024-01-01T16:00:00Z")
        whenever(dateTimeProvider.currentTime).thenReturn(stopTime)

        val intent = Intent(context, WorkTimeNotificationActionReceiver::class.java).apply {
            action = WorkTimeNotificationService.STOP_WORK_ACTION
        }

        // Act: Manually create receiver and call onReceive
        val receiver = WorkTimeNotificationActionReceiver()
        // Manually inject dependencies because we are not letting Android create the receiver
        receiver.comeEventUtils = comeEventUtils
        receiver.dateTimeProvider = dateTimeProvider
        receiver.notificationService = notificationService
        receiver.onReceive(context, intent)

        // Assert: wait for the flow to emit the updated state
        val updatedWorkDay = withTimeoutOrNull(5000) {
            workDayFlow.filterNotNull()
                .first { it.events.isNotEmpty() && it.events.first().isEnded }
        }

        assertNotNull("Test timed out waiting for work day update", updatedWorkDay)
        assertEquals(1, updatedWorkDay!!.events.size)
        val finalEvent = updatedWorkDay.events.first()
        assertNotNull(finalEvent.endDate)
        assertEquals(stopTime, finalEvent.endDate)
    }
}