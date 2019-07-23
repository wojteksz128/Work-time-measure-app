package net.wojteksz128.worktimemeasureapp.notification.base

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.util.Log
import net.wojteksz128.worktimemeasureapp.notification.action.Action
import net.wojteksz128.worktimemeasureapp.notification.service.NotificationIntentService
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity

abstract class AppNotification(val notificationId: Int, private val pendingIntentId: Int) {

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

    protected fun largeIcon(context: Context, resourceId: Int): Bitmap? {
        val resources = context.resources
        return BitmapFactory.decodeResource(resources, resourceId)
    }

    companion object {
        private val LOG = AppNotification::class.java.simpleName
    }
}
