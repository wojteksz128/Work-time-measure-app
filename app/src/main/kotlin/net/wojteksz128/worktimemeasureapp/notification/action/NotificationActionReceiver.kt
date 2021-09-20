package net.wojteksz128.worktimemeasureapp.notification.action

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let { Action.valueOf(it)(context) }
    }
}