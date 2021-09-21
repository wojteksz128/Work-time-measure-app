package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.Context
import android.util.Log
import net.wojteksz128.worktimemeasureapp.notification.NotificationActionImpl

import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

internal object IgnoreReminderActionImpl : NotificationActionImpl, ClassTagAware {

    override operator fun invoke(context: Context) {
        Log.d(classTag, "invoke: Ignore notification clicked")
        NotificationUtils.clearAllNotifications(context)
    }
}
