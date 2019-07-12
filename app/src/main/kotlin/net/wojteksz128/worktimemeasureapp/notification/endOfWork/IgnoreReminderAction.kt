package net.wojteksz128.worktimemeasureapp.notification.endOfWork

import android.content.Context
import android.util.Log

import net.wojteksz128.worktimemeasureapp.util.notification.NotificationAction
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils

internal class IgnoreReminderAction : NotificationAction {
    private val LOG = IgnoreReminderAction::class.java.simpleName

    override operator fun invoke(context: Context) {
        Log.d(LOG, "invoke: Ignore notification clicked")
        NotificationUtils.clearAllNotifications(context)
    }
}
