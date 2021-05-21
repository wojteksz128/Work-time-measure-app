package net.wojteksz128.worktimemeasureapp.notification.action

import android.content.Context
import android.util.Log

import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils

internal object IgnoreReminderAction : NotificationAction {

    private val LOG = IgnoreReminderAction::class.java.simpleName

    override operator fun invoke(context: Context) {
        Log.d(LOG, "invoke: Ignore notification clicked")
        NotificationUtils.clearAllNotifications(context)
    }
}
