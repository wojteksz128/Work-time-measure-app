package net.wojteksz128.worktimemeasureapp.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class NotificationUtils {

    private static final String LOG = NotificationUtils.class.getSimpleName();

    public static void initNotifications(Context context) {
        Log.v(LOG, "initNotifications: Initialize all notifications");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                for (Channel channel : Channel.values()) {
                    notificationManager.createNotificationChannel(channel.getNotificationChannel(context));
                }
            }

        }
    }

    public static void clearAllNotifications(Context context) {
        Log.d(LOG, "clearAllNotifications: Clearing notifications started.");
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
            Log.i(LOG, "clearAllNotifications: All notifications cleared.");
        }
    }
}
