package net.wojteksz128.worktimemeasureapp.notification

import android.app.Notification
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.action.Action

class EndOfWorkNotification(context: Context) : AppNotification(251, 481, context) {

    init {
        Log.d(LOG, "init: Init notificationBuilder")
        notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon(context, R.drawable.ic_launcher_foreground))
                .setContentTitle(context.getString(R.string.notification_end_of_work_title))
                .setContentText(context.getString(R.string.notification_end_of_work_text))
                .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_end_of_work_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(getAction(context, Action.END_OF_WORK_ACTION))
                .addAction(getAction(context, Action.IGNORE_REMINDER_ACTION))
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.priority = NotificationManagerCompat.IMPORTANCE_HIGH
        }
    }

    companion object {
        private val LOG = EndOfWorkNotification::class.java.simpleName
        private val CHANNEL_ID = Channel.END_OF_WORK_CHANNEL.id
    }
}
