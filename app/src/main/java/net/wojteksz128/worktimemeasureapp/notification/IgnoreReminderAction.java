package net.wojteksz128.worktimemeasureapp.notification;

import android.content.Context;
import android.util.Log;

class IgnoreReminderAction implements NotificationAction {

    private static final String LOG = IgnoreReminderAction.class.getSimpleName();

    @Override
    public void invoke(Context context) {
        Log.d(LOG, "invoke: Ignore notification clicked");
        NotificationUtils.clearAllNotifications(context);
    }
}
