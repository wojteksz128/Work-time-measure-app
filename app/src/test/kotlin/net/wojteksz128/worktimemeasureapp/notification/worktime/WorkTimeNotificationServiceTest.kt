package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.PendingIntent
import android.content.Context
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.item.BooleanSettingsItem
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.fixtures.TestFixtures
import net.wojteksz128.worktimemeasureapp.util.fixtures.aInProgressState
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.threeten.bp.ZoneId
import org.threeten.bp.zone.ZoneRules
import org.threeten.bp.zone.ZoneRulesProvider
import java.util.NavigableMap

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

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            if (ZoneRulesProvider.getAvailableZoneIds().isEmpty()) {
                try {
                    Class.forName("org.threeten.bp.TLSZoneRulesProvider")
                } catch (_: ClassNotFoundException) {
                    ZoneRulesProvider.registerProvider(SimpleZoneRulesProvider())
                }
            }
        }
    }


    @Before
    fun setUp() {
        whenever(settings.WorkTime).thenReturn(workTimeSettings)
        whenever(workTimeSettings.NotifyingEnabled).thenReturn(notifyingEnabledItem)

        val realService = WorkTimeNotificationService(
            context,
            timerManager,
            notificationFactory,
            settings
        )
        workTimeNotificationService = spy(realService)
    }

    @Test
    fun `givenNotifyingEnabled, whenShowWorkInProgressNotification, thenNotificationIsShown`() {
        // Arrange
        val inProgressState = aInProgressState()
        whenever(notifyingEnabledItem.value).thenReturn(true)
        whenever(
            notificationFactory.createWorkInProgressNotification(
                inProgressState
            )
        ).thenReturn(notification)

        // Act
        workTimeNotificationService.showWorkInProgressNotification(inProgressState)

        // Assert
        verify(notificationFactory).createWorkInProgressNotification(inProgressState)
        verify(notification).show()
    }

    @Test
    fun `givenNotifyingDisabled, whenShowWorkInProgressNotification, thenNotificationIsNotShown`() {
        // Arrange
        val inProgressState = aInProgressState()
        whenever(notifyingEnabledItem.value).thenReturn(false)

        // Act
        workTimeNotificationService.showWorkInProgressNotification(inProgressState)

        // Assert
        verify(notificationFactory, never()).createWorkInProgressNotification(any())
    }

    @Test
    fun `givenNotifyingEnabled, whenScheduleEndOfWorkNotification, thenTimerIsScheduled`() {
        // Arrange
        val endTime = TestFixtures.DEFAULT_START_TIME // Use fixed time
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
        val endTime = TestFixtures.DEFAULT_START_TIME // Use fixed time
        whenever(notifyingEnabledItem.value).thenReturn(false)

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(endTime, null, null)

        // Assert
        verify(timerManager, never()).setExactTimer(any(), any())
    }

    @Test
    fun `givenNotifyingEnabled, whenScheduleEndOfWorkNotification with workday, thenTimerIsScheduled`() {
        // Arrange
        val now = TestFixtures.DEFAULT_START_TIME.plusHours(7)
        val inProgressState = aInProgressState {
            currentTime = now
        }
        whenever(dateTimeProvider.currentTime).thenReturn(now)
        whenever(notifyingEnabledItem.value).thenReturn(true)
        val pendingIntentMock = mock<PendingIntent>()
        doReturn(pendingIntentMock).whenever(workTimeNotificationService)
            .createTimerExpiredPendingIntent(anyOrNull(), anyOrNull())

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(inProgressState)

        // Assert
        verify(timerManager).setExactTimer(any(), any())
    }

    @Test
    fun `givenNotifyingDisabled, whenScheduleEndOfWorkNotification with workday, thenTimerIsNotScheduled`() {
        // Arrange
        whenever(notifyingEnabledItem.value).thenReturn(false)

        // Add missing mocks to prevent NullPointerException
        val now = TestFixtures.DEFAULT_START_TIME.plusHours(7)
        val workTimeRequirements = aInProgressState {
            currentTime = now
        }

        // Act
        workTimeNotificationService.scheduleEndOfWorkNotification(workTimeRequirements)

        // Assert
        verify(timerManager, never()).setExactTimer(any(), any())
    }
}

private class SimpleZoneRulesProvider : ZoneRulesProvider() {

    override fun provideZoneIds(): Set<String?> = setOf("UTC", "GMT", "Europe/Warsaw")

    override fun provideRules(regionId: String?, forCaching: Boolean): ZoneRules? {
        return ZoneId.of("UTC").rules
    }

    override fun provideVersions(zoneId: String?): NavigableMap<String?, ZoneRules?> {
        return java.util.TreeMap()
    }
}
