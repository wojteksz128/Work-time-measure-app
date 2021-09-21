package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WorkTimeNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let { WorkTimeNotificationAction.valueOf(it)(context) }
    }
}