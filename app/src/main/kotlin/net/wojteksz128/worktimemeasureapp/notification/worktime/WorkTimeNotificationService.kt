package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.TimerExpiredReceiver
import net.wojteksz128.worktimemeasureapp.notification.TimerExpiredReceiver.Companion.EXTRA_BALANCED_END_TIME
import net.wojteksz128.worktimemeasureapp.notification.TimerExpiredReceiver.Companion.EXTRA_STANDARD_END_TIME
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTimeNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val timerManager: TimerManager,
    private val notificationFactory: WorkTimeNotificationFactory,
    private val settings: Settings,
) {
    companion object {
        const val SNOOZE_ACTION = "net.wojteksz128.worktimemeasureapp.SNOOZE_ACTION"
        const val STOP_WORK_ACTION = "net.wojteksz128.worktimemeasureapp.STOP_WORK_ACTION"
        const val SNOOZE_TO_NEXT_ACTION = "net.wojteksz128.worktimemeasureapp.SNOOZE_TO_NEXT_ACTION"
    }

    fun showWorkInProgressNotification(workDay: WorkDay, workTimeBalance: WorkTimeBalance) {
        val isEnabled = settings.WorkTime.NotifyingEnabled.value
        if (!isEnabled) return

        val notification =
            notificationFactory.createWorkInProgressNotification(workDay, workTimeBalance)
        notification.show()
    }

    fun cancelWorkInProgressNotification() {
        WorkTimeInProgressNotification.cancel(context)
    }

    fun showEndOfWorkNotification() {
        val notification = notificationFactory.createEndOfWorkNotification()
        notification.show()
    }

    fun showEndOfWorkNotification(standardEndTime: ZonedDateTime, balancedEndTime: ZonedDateTime) {
        val notification =
            notificationFactory.createEndOfWorkNotification(standardEndTime, balancedEndTime)
        notification.show()
    }

    fun scheduleEndOfWorkNotification(endTime: ZonedDateTime) {
        scheduleEndOfWorkNotification(endTime, null, null)
    }

    fun scheduleEndOfWorkNotification(
        endTime: ZonedDateTime,
        standardEndTime: ZonedDateTime?,
        balancedEndTime: ZonedDateTime?,
    ) {
        val isEnabled = settings.WorkTime.NotifyingEnabled.value
        if (!isEnabled) return

        val pendingIntent = createTimerExpiredPendingIntent(standardEndTime, balancedEndTime)
        timerManager.setExactTimer(endTime, pendingIntent)
    }

    fun scheduleEndOfWorkNotification(workDay: WorkDay, workTimeBalance: WorkTimeBalance) {
        val standardEndTime = workTimeBalance.getStandardEndTime(workDay)
        val balancedEndTime = workTimeBalance.getBalancedEndTime(workDay)
        val notificationTime = listOf(standardEndTime, balancedEndTime)
            .filter { it.isAfter(dateTimeProvider.currentTime) }
            .minOrNull() ?: dateTimeProvider.currentTime
        scheduleEndOfWorkNotification(notificationTime, standardEndTime, balancedEndTime)
    }

    fun cancelEndOfWorkNotification() {
        val pendingIntent = createTimerExpiredPendingIntent()
        timerManager.removeAlarm(pendingIntent)
        EndOfWorkTimeNotification.cancel(context)
    }

    private fun createTimerExpiredPendingIntent(): PendingIntent {
        val intent = Intent(context, TimerExpiredReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            TimerExpiredReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createTimerExpiredPendingIntent(
        standardEndTime: ZonedDateTime?,
        balancedEndTime: ZonedDateTime?,
    ): PendingIntent {
        val intent = Intent(context, TimerExpiredReceiver::class.java).apply {
            standardEndTime?.let { putExtra(EXTRA_STANDARD_END_TIME, it.toString()) }
            balancedEndTime?.let { putExtra(EXTRA_BALANCED_END_TIME, it.toString()) }
        }
        return PendingIntent.getBroadcast(
            context,
            TimerExpiredReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}