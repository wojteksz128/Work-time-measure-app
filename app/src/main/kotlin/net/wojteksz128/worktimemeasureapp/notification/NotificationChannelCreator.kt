package net.wojteksz128.worktimemeasureapp.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class NotificationChannelCreator(
    private val context: Context,
) : ClassTagAware {

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun initNotifications() {
        Log.v(classTag, "initNotifications: Initialize all notification channels")

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (notificationManager != null) {
            for (channel in Channel.entries) {
                notificationManager.createNotificationChannel(
                    channel.getNotificationChannel(
                        context
                    )
                )
            }
        }

    }

}
