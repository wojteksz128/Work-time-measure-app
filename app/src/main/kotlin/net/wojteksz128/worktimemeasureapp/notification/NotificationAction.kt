package net.wojteksz128.worktimemeasureapp.notification

import android.content.BroadcastReceiver
import android.content.Context

interface NotificationAction<T> where T : BroadcastReceiver {
    val name: String
    val notificationAction: NotificationActionImpl
    val icon: Int
    val title: Int

    operator fun invoke(context: Context) {
        notificationAction(context)
    }
}