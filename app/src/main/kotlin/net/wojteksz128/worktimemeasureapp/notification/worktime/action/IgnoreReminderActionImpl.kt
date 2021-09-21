package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import net.wojteksz128.worktimemeasureapp.notification.NotificationActionImpl
import net.wojteksz128.worktimemeasureapp.notification.worktime.EndOfWorkTimeNotification
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeInProgressNotification
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

internal object IgnoreReminderActionImpl : NotificationActionImpl, ClassTagAware {

    override operator fun invoke(context: Context) {
        Log.d(classTag, "invoke: Ignore Reminder action clicked")
        NotificationManagerCompat.from(context).cancel(WorkTimeInProgressNotification.notificationId)
        NotificationManagerCompat.from(context).cancel(EndOfWorkTimeNotification.notificationId)
    }
}
