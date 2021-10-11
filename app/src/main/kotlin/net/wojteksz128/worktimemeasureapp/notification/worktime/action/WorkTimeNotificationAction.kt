package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.di.endOfWorkActionName
import net.wojteksz128.worktimemeasureapp.di.ignoreReminderActionName
import net.wojteksz128.worktimemeasureapp.notification.NotificationAction

enum class WorkTimeNotificationAction(
    override val notificationActionName: String,
    override val icon: Int,
    override val title: Int,
) : NotificationAction<WorkTimeNotificationActionReceiver> {
    END_OF_WORK_ACTION(endOfWorkActionName, R.drawable.come_out_background, R.string.notification_end_of_work_action_come_out),
    IGNORE_REMINDER_ACTION(ignoreReminderActionName, R.drawable.come_in_background, R.string.notification_end_of_work_action_ignore);
}