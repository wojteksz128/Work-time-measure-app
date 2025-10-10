package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.TimerExpiredReceiver
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTimeNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val timerManager: TimerManager,
    private val notificationFactory: WorkTimeNotificationFactory,
) {
    companion object {
        const val SNOOZE_ACTION = "net.wojteksz128.worktimemeasureapp.SNOOZE_ACTION"
        const val STOP_WORK_ACTION = "net.wojteksz128.worktimemeasureapp.STOP_WORK_ACTION"
        const val EXTEND = "net.wojteksz128.worktimemeasureapp.EXTEND_ACTION"
    }

    fun showWorkInProgressNotification(workDay: WorkDay, workTimeBalance: WorkTimeBalance) {
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

    fun scheduleEndOfWorkNotification(endTime: ZonedDateTime) {
        val pendingIntent = createTimerExpiredPendingIntent()
        timerManager.setExactTimer(endTime, pendingIntent)
    }

    fun scheduleEndOfWorkNotification(workDay: WorkDay, workTimeBalance: WorkTimeBalance) {
        val startTime = workDay.events.lastOrNull()?.startDate
        val balancedEndTime = startTime?.plus(workTimeBalance.remainingTodayWorkTime)
            ?.plus(workTimeBalance.monthlyBalance)
        balancedEndTime?.let { scheduleEndOfWorkNotification(it) }
    }

    fun cancelEndOfWorkNotification() {
        val pendingIntent = createTimerExpiredPendingIntent()
        timerManager.removeAlarm(pendingIntent)
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
}