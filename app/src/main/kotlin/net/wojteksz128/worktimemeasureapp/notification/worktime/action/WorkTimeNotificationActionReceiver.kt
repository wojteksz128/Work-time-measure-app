package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.di.endOfWorkActionName
import net.wojteksz128.worktimemeasureapp.di.ignoreReminderActionName
import net.wojteksz128.worktimemeasureapp.notification.HiltBroadcastReceiver
import net.wojteksz128.worktimemeasureapp.notification.NotificationActionImpl
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class WorkTimeNotificationActionReceiver : HiltBroadcastReceiver() {

    @Inject
    @Named(endOfWorkActionName)
    lateinit var endOfWorkActionImpl: NotificationActionImpl

    @Inject
    @Named(ignoreReminderActionName)
    lateinit var ignoreReminderActionImpl: NotificationActionImpl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        intent.action?.let { actionName ->
            when(WorkTimeNotificationAction.valueOf(actionName)) {
                WorkTimeNotificationAction.END_OF_WORK_ACTION -> endOfWorkActionImpl(context)
                WorkTimeNotificationAction.IGNORE_REMINDER_ACTION -> ignoreReminderActionImpl(context)
            }
        }
    }
}