package net.wojteksz128.worktimemeasureapp.notification.action

import android.content.Context
import android.util.Log

import net.wojteksz128.worktimemeasureapp.util.notification.NotificationAction
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils

internal class IgnoreReminderAction : NotificationAction {

    override operator fun invoke(context: Context) {
        Log.d(LOG, "invoke: Ignore notification clicked")
        NotificationUtils.clearAllNotifications(context)
    }

    companion object {
        private val LOG = IgnoreReminderAction::class.java.simpleName
    }
}
