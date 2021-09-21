package net.wojteksz128.worktimemeasureapp.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

abstract class AppNotification<T>(
    val channel: Channel,
    private val notificationId: Int,
    private val context: Context,
) : ClassTagAware where T : BroadcastReceiver {
    protected val notificationBuilder: NotificationCompat.Builder

    init {
        notificationBuilder = NotificationCompat.Builder(context, channel.id)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIcon(context, R.drawable.ic_launcher_foreground))
            .setAutoCancel(true)
    }

    fun notifyUser() {
        Log.d(classTag, "notifyUser: AppNotification notifying ${this::class.java.simpleName}")
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }

    protected fun <T> getPendingIntentWithStack(
        context: Context,
        javaClass: Class<T>,
    ): PendingIntent {
        val intent = Intent(context, javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val stackBuilder = TaskStackBuilder.create(context).apply {
            addParentStack(javaClass)
            addNextIntent(intent)
        }

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)!!
    }

    protected inline fun <reified T : BroadcastReceiver> getAction(
        context: Context,
        action: NotificationAction<T>,
    ): NotificationCompat.Action {
        Log.v(classTag, "getAction: Create action ${action.name} for ${this.javaClass.simpleName}")
        val intent = Intent(context, T::class.java).apply {
            this.action = action.name
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(action.icon,
            context.getString(action.title),
            pendingIntent)
    }

    private fun largeIcon(
        context: Context,
        @Suppress("SameParameterValue") resourceId: Int,
    ): Bitmap? {
        val resources = context.resources
        return BitmapFactory.decodeResource(resources, resourceId)
    }
}
