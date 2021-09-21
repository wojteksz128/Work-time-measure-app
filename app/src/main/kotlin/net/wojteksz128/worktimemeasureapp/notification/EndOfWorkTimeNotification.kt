package net.wojteksz128.worktimemeasureapp.notification

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.action.Action
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity

class EndOfWorkTimeNotification(context: Context) :
    AppNotification(Channel.WORK_TIME_CHANNEL, 251, context), ClassTagAware {

    init {
        Log.d(classTag, "init: Init notificationBuilder")
        notificationBuilder.setContentTitle(context.getString(R.string.notification_end_of_work_title))
            .setContentText(context.getString(R.string.notification_end_of_work_text))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(context.getString(R.string.notification_end_of_work_text)))
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setContentIntent(getPendingIntentWithStack(context, DashboardActivity::class.java))
            .addAction(getAction(context, Action.END_OF_WORK_ACTION))
            .addAction(getAction(context, Action.IGNORE_REMINDER_ACTION))

        notificationBuilder.priority = channel.importance
    }
}
