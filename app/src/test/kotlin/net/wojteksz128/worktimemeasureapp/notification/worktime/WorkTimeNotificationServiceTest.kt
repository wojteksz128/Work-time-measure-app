package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.PendingIntent
import android.content.Context
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.item.BooleanSettingsItem
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

class WorkTimeNotificationServiceTest {

    private val context: Context = mock()
    private val dateTimeProvider: DateTimeProvider = mock()
    private val timerManager: TimerManager = mock()
    private val notificationFactory: WorkTimeNotificationFactory = mock()
    private val settings: Settings = mock()

    private val notifyingEnabledItem: BooleanSettingsItem = mock()
    private val workTimeSettings: Settings.WorkTimeSettings = mock()
    private val notification: WorkTimeInProgressNotification = mock()

    private lateinit var workTimeNotificationService: WorkTimeNotificationService

    // Using a fixed time for tests makes them deterministic and avoids timezone issues on the JVM
    private val testTime: ZonedDateTime = ZonedDateTime.of(2024, 1, 10, 12, 0, 0, 0, ZoneOffset.UTC)


    @Before
    fun setUp() {
        whenever(settings.WorkTime).thenReturn(workTimeSettings)
        whenever(workTimeSettings.NotifyingEnabled).thenReturn(notifyingEnabledItem)

        val realService = WorkTimeNotificationService(
            context,
            dateTimeProvider,
            timerManager,
            notificationFactory,
            settings
        )
        workTimeNotificationService = spy(realService)
    }

    @Test
    fun `givenNotifyingEnabled, whenShowWorkInProgressNotification, thenNotificationIsShown`() {
        // Arrange
        val workDay = mock<WorkDay>()
        val workTimeBalance = mock<WorkTimeBalance>()
        whenever(notifyingEnabledItem.value).thenReturn(true)
        whenever(
            notificationFactory.createWorkInProgressNotification(
                workDay,
                workTimeBalance
            )
        ).thenReturn(notification)

        // Act
        workTimeNotificationService.showWorkInProgressNotification(workDay, workTimeBalance)

        // Assert
        verify(notificationFactory).createWorkInProgressNotification(workDay, workTimeBalance)
        verify(notification).show()
    }

    @Test
    fun `givenNotifyingDisabled, whenShowWorkInProgressNotification, thenNotificationIsNotShown`() {
        // Arrange
        val workDay = mock<WorkDay>()
        val workTimeBalance = mock<WorkTimeBalance>()
        whenever(notifyingEnabledItem.value).thenReturn(false)

        // Act
        workTimeNotificationService.showWorkInProgressNotification(workDay, workTimeBalance)

        // Assert
        verify(notificationFactory, never()).createWorkInProgressNotification(any(), any())
    }

    @Test
    fun `givenNotifyingEnabled, whenScheduleEndOfWorkNotification, thenTimerIsScheduled`() {
        // Arrange
        val endTime = testTime // Use fixed time
        whenever(notifyingEnabledItem.value).thenReturn(true)
        val pendingIntentMock = mock<PendingIntent>()
        doReturn(pendingIntentMock).whenever(workTimeNotificationService)
            .createTimerExpiredPendingIntent(anyOrNull(), anyOrNull())

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(endTime, null, null)

        // Assert
        verify(timerManager).setExactTimer(any(), any())
    }

    @Test
    fun `givenNotifyingDisabled, whenScheduleEndOfWorkNotification, thenTimerIsNotScheduled`() {
        // Arrange
        val endTime = testTime // Use fixed time
        whenever(notifyingEnabledItem.value).thenReturn(false)

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(endTime, null, null)

        // Assert
        verify(timerManager, never()).setExactTimer(any(), any())
    }

    @Test
    fun `givenNotifyingEnabled, whenScheduleEndOfWorkNotification with workday, thenTimerIsScheduled`() {
        // Arrange
        val workTimeBalance = mock<WorkTimeBalance>()
        val now = testTime // Use fixed time
        val later = now.plusHours(1)
        whenever(dateTimeProvider.currentTime).thenReturn(now)
        whenever(workTimeBalance.standardEndTime).thenReturn(later)
        whenever(workTimeBalance.balancedEndTime).thenReturn(later)
        whenever(notifyingEnabledItem.value).thenReturn(true)
        val pendingIntentMock = mock<PendingIntent>()
        doReturn(pendingIntentMock).whenever(workTimeNotificationService)
            .createTimerExpiredPendingIntent(anyOrNull(), anyOrNull())

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(workTimeBalance)

        // Assert
        verify(timerManager).setExactTimer(any(), any())
    }

    @Test
    fun `givenNotifyingDisabled, whenScheduleEndOfWorkNotification with workday, thenTimerIsNotScheduled`() {
        // Arrange
        val workTimeBalance = mock<WorkTimeBalance>()
        whenever(notifyingEnabledItem.value).thenReturn(false)

        // Add missing mocks to prevent NullPointerException
        val now = testTime
        val later = now.plusHours(1)
        whenever(dateTimeProvider.currentTime).thenReturn(now)
        whenever(workTimeBalance.standardEndTime).thenReturn(later)
        whenever(workTimeBalance.balancedEndTime).thenReturn(later)

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(workTimeBalance)

        // Assert
        verify(timerManager, never()).setExactTimer(any(), any())
    }
}
