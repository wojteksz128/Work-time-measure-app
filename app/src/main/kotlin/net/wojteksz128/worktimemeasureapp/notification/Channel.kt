package net.wojteksz128.worktimemeasureapp.notification

import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

import net.wojteksz128.worktimemeasureapp.R

// TODO: 17.09.2021 Czy na pewno wszystkie te kanały są potrzebne?
@Suppress("MemberVisibilityCanBePrivate")
enum class Channel(
    val id: String,
    val channelName: Int,
    val description: Int,
    val importance: Int,
) {
    END_OF_WORK_CHANNEL("end-of-work-channel",
        R.string.channel_end_of_work_name,
        R.string.channel_end_of_work_description,
        4/*NotificationManager.IMPORTANCE_HIGH*/),
    IN_WORK_CHANNEL("in-work-channel",
        R.string.channel_end_of_work_name,
        R.string.channel_end_of_work_description,
        3/*NotificationManager.IMPORTANCE_DEFAULT*/);

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
