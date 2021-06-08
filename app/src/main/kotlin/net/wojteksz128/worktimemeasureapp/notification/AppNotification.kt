package net.wojteksz128.worktimemeasureapp.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.wojteksz128.worktimemeasureapp.notification.action.Action
import net.wojteksz128.worktimemeasureapp.notification.action.NotificationIntentService
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity

abstract class AppNotification(private val notificationId: Int, private val pendingIntentId: Int, private val context: Context) {
    protected lateinit var notificationBuilder: NotificationCompat.Builder

    fun notifyUser() {
        Log.d(LOG, "notifyUser: AppNotification notifying ${this::class.java.simpleName}")
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg p0: Unit?) {
                NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
            }
        }.execute()
    }

    protected fun getAction(context: Context, action: Action): NotificationCompat.Action {
        Log.v(LOG, "getAction: Create action ${action.name} for ${this.javaClass.simpleName}")
        val intent = Intent(context, NotificationIntentService::class.java)
        intent.action = action.name
        val pendingIntent = PendingIntent.getService(context, action.pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Action(action.icon, context.getString(action.title), pendingIntent)
    }

    protected fun contentIntent(context: Context): PendingIntent {
        val intent = Intent(context, DashboardActivity::class.java)
        return PendingIntent.getActivity(context, pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    protected fun largeIcon(context: Context, @Suppress("SameParameterValue") resourceId: Int): Bitmap? {
        val resources = context.resources
        return BitmapFactory.decodeResource(resources, resourceId)
    }

    companion object {
        private val LOG = AppNotification::class.java.simpleName
    }
}
