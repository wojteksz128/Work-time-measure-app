package net.wojteksz128.worktimemeasureapp.util.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.WorkTimeNotificationFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.window.dashboard.WorkTimeData
import java.util.*

object NotificationUtils {
    private val LOG = NotificationUtils::class.java.simpleName

    fun initNotifications(context: Context) {
        Log.v(LOG, "initNotifications: Initialize all notifications")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                for (channel in Channel.values()) {
                    notificationManager.createNotificationChannel(channel.getNotificationChannel(context))
                }
            }

        }
    }

    fun notifyUserAboutWorkTime(context: Context, workTimeData: WorkTimeData) {
        val endOfWorkTimeExpired = DateTimeProvider.currentCalendarWithoutCorrection.apply {
            val remainingTodayWorkTimeMillis = workTimeData.remainingTodayWorkTime?.toMillis() ?: 0L
            add(Calendar.MILLISECOND, remainingTodayWorkTimeMillis.toInt())
        }
        val expectedEndWorkDayTime = workTimeData.expectedEndWorkDayTime ?: Date()

        WorkTimeNotificationFactory.scheduleEndOfWorkTimeNotification(context, endOfWorkTimeExpired)
        WorkTimeNotificationFactory.showWorkTimeInProgressNotification(context,
            expectedEndWorkDayTime)
    }

    fun clearAllNotifications(context: Context) {
        Log.d(LOG, "clearAllNotifications: Clearing notifications started.")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        Log.i(LOG, "clearAllNotifications: All notifications cleared.")
    }
}
