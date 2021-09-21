package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import java.util.*

object WorkTimeNotificationFactory {

    fun showWorkTimeInProgressNotification(context: Context, endOfWorkTime: Date) {
        WorkTimeInProgressNotification(context, endOfWorkTime).notifyUser()
    }

    fun scheduleEndOfWorkTimeNotification(context: Context, endOfWorkTimeExpired: Calendar) {
        TimerManager.setAlarm(context, endOfWorkTimeExpired)
    }

    fun showEndOfWorkTimeNotification(context: Context) {
        EndOfWorkTimeNotification(context).notifyUser()
    }
}