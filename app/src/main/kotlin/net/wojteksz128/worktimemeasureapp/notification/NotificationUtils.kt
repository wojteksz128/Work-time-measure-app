package net.wojteksz128.worktimemeasureapp.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationFactory
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.dashboard.WorkTimeData
import java.util.*

class NotificationUtils(
    private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val timerManager: TimerManager,
    private val dateTimeUtils: DateTimeUtils
) : ClassTagAware {

    fun initNotifications() {
        Log.v(classTag, "initNotifications: Initialize all notifications")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                for (channel in Channel.values()) {
                    notificationManager.createNotificationChannel(channel.getNotificationChannel(context))
                }
            }

        }
    }

    fun notifyUserAboutWorkTime(workTimeData: WorkTimeData) {
        val endOfWorkTimeExpired = dateTimeProvider.currentCalendarWithoutCorrection.apply {
            val remainingTodayWorkTimeMillis = workTimeData.remainingTodayWorkTime?.toMillis() ?: 0L
            add(Calendar.MILLISECOND, remainingTodayWorkTimeMillis.toInt())
        }
        val expectedEndWorkDayTime = workTimeData.expectedEndWorkDayTime ?: Date()

        scheduleEndOfWorkTimeNotification(endOfWorkTimeExpired)
        if (dateTimeProvider.currentTime.before(endOfWorkTimeExpired.time))
            WorkTimeNotificationFactory.createWorkTimeInProgressNotification(
                context,
                expectedEndWorkDayTime,
                dateTimeUtils
            ).notifyUser()
    }

    private fun scheduleEndOfWorkTimeNotification(endOfWorkTimeExpired: Calendar) {
        timerManager.setAlarm(endOfWorkTimeExpired)
    }

    fun clearAllNotifications() {
        Log.d(classTag, "clearAllNotifications: Clearing notifications started.")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        Log.i(classTag, "clearAllNotifications: All notifications cleared.")
    }
}
