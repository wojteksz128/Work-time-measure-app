package net.wojteksz128.worktimemeasureapp.notification

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.action.Action
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import java.util.*

class WorkTimeInProgressNotification(context: Context, endOfWorkTime: Date) : AppNotification(Channel.IN_WORK_CHANNEL, 252, context),
    ClassTagAware {

    init {
        Log.d(classTag, "init: Init notification builder")
        val contextText = context.getString(R.string.notification_in_work_text, DateTimeUtils.formatDate("HH:mm", endOfWorkTime))
        notificationBuilder.setContentTitle(context.getString(R.string.notification_in_work_title))
            .setContentText(contextText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(contextText))
            .setOngoing(true)
            .setContentIntent(getPendingIntentWithStack(context, DashboardActivity::class.java))
            .addAction(getAction(context, Action.END_OF_WORK_ACTION))

        notificationBuilder.priority = channel.importance
    }
}
