package net.wojteksz128.worktimemeasureapp.notification.worktime.action

import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.notification.NotificationAction
import net.wojteksz128.worktimemeasureapp.notification.NotificationActionImpl

enum class WorkTimeNotificationAction(
    override val notificationAction: NotificationActionImpl,
    override val icon: Int,
    override val title: Int,
) : NotificationAction<WorkTimeNotificationActionReceiver> {
    END_OF_WORK_ACTION(EndOfWorkActionImpl, R.drawable.come_out_background, R.string.notification_end_of_work_action_come_out),
    IGNORE_REMINDER_ACTION(IgnoreReminderActionImpl, R.drawable.come_in_background, R.string.notification_end_of_work_action_ignore);
}