package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.AppNotification
import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationAction
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.WorkTimeNotificationActionReceiver
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import java.util.*

class WorkTimeInProgressNotification(context: Context, endOfWorkTime: Date) :
    AppNotification<WorkTimeNotificationActionReceiver>(
        Channel.WORK_TIME_CHANNEL, notificationId, context),
    ClassTagAware {

    init {
        Log.d(classTag, "init: Init notification builder")
        val contextText = context.getString(R.string.notification_in_work_text,
            DateTimeUtils.formatDate("HH:mm", endOfWorkTime))
        notificationBuilder.setContentTitle(context.getString(R.string.notification_in_work_title))
            .setContentText(contextText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(contextText))
            .setOngoing(true)
            .setContentIntent(getPendingIntentWithStack(context, DashboardActivity::class.java))
            .addAction(getAction(context, WorkTimeNotificationAction.END_OF_WORK_ACTION))

        notificationBuilder.priority = channel.importance
    }

    companion object {
        const val notificationId = 251
    }
}
