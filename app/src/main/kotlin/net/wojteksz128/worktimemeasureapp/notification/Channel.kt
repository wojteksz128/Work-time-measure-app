package net.wojteksz128.worktimemeasureapp.notification

import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat

import net.wojteksz128.worktimemeasureapp.R

@Suppress("MemberVisibilityCanBePrivate")
enum class Channel(
    val id: String,
    @StringRes val channelName: Int,
    @StringRes val description: Int,
    val importance: Int,
) {
    END_WORK_TIME_CHANNEL(
        "end-work-time-channel",
        R.string.channel_end_work_time_name,
        R.string.channel_end_work_time_description,
        NotificationManagerCompat.IMPORTANCE_HIGH
    ),
    WORK_TIME_IN_PROGRESS_CHANNEL(
        "work-time-in-progress-channel",
        R.string.channel_work_time_in_progress_name,
        R.string.channel_work_time_in_progress_description,
        NotificationManagerCompat.IMPORTANCE_DEFAULT
    );

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotificationChannel(context: Context): NotificationChannel {
        val name = context.getString(this.channelName)
        val description = context.getString(this.description)

        val notificationChannel = NotificationChannel(id, name, importance)
        notificationChannel.description = description
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE

        return notificationChannel
    }
}
