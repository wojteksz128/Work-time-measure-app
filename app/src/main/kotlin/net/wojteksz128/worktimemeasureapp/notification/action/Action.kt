package net.wojteksz128.worktimemeasureapp.notification.action

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R

enum class Action(private val notificationAction: NotificationAction, val pendingIntentId: Int, val icon: Int, val title: Int) {
    END_OF_WORK_ACTION(EndOfWorkAction, 60, R.drawable.come_out_background, R.string.notification_end_of_work_action_come_out),
    IGNORE_REMINDER_ACTION(IgnoreReminderAction, 871, R.drawable.come_in_background, R.string.notification_end_of_work_action_ingore);

    operator fun invoke(context: Context) {
        this.notificationAction(context)
    }
}