package net.wojteksz128.worktimemeasureapp.notification.endOfWork

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.Channel
import net.wojteksz128.worktimemeasureapp.notification.Notification.END_OF_WORK
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationAction
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import java.text.MessageFormat

object EndOfWorkNotification {

    private val LOG = EndOfWorkNotification::class.java.simpleName

    private val CHANNEL_ID = Channel.END_OF_WORK_CHANNEL.id

    fun createNotification(context: Context) {
        Log.d(LOG, "createNotification: Create notification")
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon(context))
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

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d(LOG, "createNotification: Notification notifying")
        notificationManager.notify(END_OF_WORK.notificationId, notificationBuilder.build())
    }

    private fun getAction(context: Context, action: Action): NotificationCompat.Action {
        Log.v(LOG, MessageFormat.format("getAction: Create action {0}", action.name))
        val intent = Intent(context, EndOfWorkNotificationIntentService::class.java)
        intent.action = action.name
        val pendingIntent = PendingIntent.getService(context, action.pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(action.icon, context.getString(action.title), pendingIntent)
    }

    private fun contentIntent(context: Context): PendingIntent {
        val intent = Intent(context, DashboardActivity::class.java)
        return PendingIntent.getActivity(context, END_OF_WORK.pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun largeIcon(context: Context): Bitmap? {
        val resources = context.resources
        return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)
    }

    enum class Action(private val notificationAction: NotificationAction, val pendingIntentId: Int, val icon: Int, val title: Int) {
        END_OF_WORK_ACTION(EndOfWorkAction(), 60, R.drawable.come_out_background, R.string.notification_end_of_work_action_come_out),
        IGNORE_REMINDER_ACTION(IgnoreReminderAction(), 871, R.drawable.come_in_background, R.string.notification_end_of_work_action_ingore);

        fun doAction(context: Context) {
            this.notificationAction.invoke(context)
        }
    }
}
