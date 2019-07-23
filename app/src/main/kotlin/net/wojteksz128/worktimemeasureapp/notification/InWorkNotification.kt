package net.wojteksz128.worktimemeasureapp.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.action.Action
import net.wojteksz128.worktimemeasureapp.notification.base.AppNotification

object InWorkNotification : AppNotification(252, 482) {

    private val LOG = InWorkNotification::class.java.simpleName

    private val CHANNEL_ID = Channel.IN_WORK_CHANNEL.id

    fun createNotification(context: Context) {
        Log.d(LOG, "createNotification: Create notification")
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon(context, R.drawable.ic_launcher_foreground))
                .setContentTitle(context.getString(R.string.notification_in_work_title))
                .setContentText(context.getString(R.string.notification_in_work_text))
                .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_in_work_text)))
                .setDefaults(Notification.DEFAULT_ALL)
                .setOngoing(true)
                .setContentIntent(contentIntent(context))
                .addAction(getAction(context, Action.END_OF_WORK_ACTION))
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.priority = NotificationManagerCompat.IMPORTANCE_HIGH
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d(LOG, "createNotification: AppNotification notifying")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
