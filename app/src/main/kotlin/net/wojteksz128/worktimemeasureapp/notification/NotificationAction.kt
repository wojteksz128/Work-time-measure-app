package net.wojteksz128.worktimemeasureapp.notification

import android.content.BroadcastReceiver

interface NotificationAction<T> where T : BroadcastReceiver {
    val name: String
    val notificationActionName: String
    val icon: Int
    val title: Int
}