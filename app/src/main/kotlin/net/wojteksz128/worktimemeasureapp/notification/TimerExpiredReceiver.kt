package net.wojteksz128.worktimemeasureapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationFactory

class TimerExpiredReceiver : BroadcastReceiver() {

    // TODO: 21.09.2021 Register expired recipients
    override fun onReceive(context: Context, intent: Intent) {
        WorkTimeNotificationFactory.createEndOfWorkTimeNotification(context).notifyUser()
    }
}