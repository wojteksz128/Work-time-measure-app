package net.wojteksz128.worktimemeasureapp.notification;

import android.content.Context;

public enum NotificationTask {
    END_OF_WORK_ACTION(new EndOfWorkAction());

    private final NotificationAction notificationAction;


    NotificationTask(NotificationAction notificationAction) {
        this.notificationAction = notificationAction;
    }

    public void doAction(Context context) {
        notificationAction.invoke(context);
    }
}
