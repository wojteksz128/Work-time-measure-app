package net.wojteksz128.worktimemeasureapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import net.wojteksz128.worktimemeasureapp.R;
import net.wojteksz128.worktimemeasureapp.window.main.MainActivity;

public class EndOfWorkNotification {

    private static final int END_OF_WORK_PENDING_INTENT_ID = 481;
    private static final int END_OF_WORK_NOTIFICATION_ID = 251;
    private static final String CHANNEL_ID = "test-channel";

    public static void create(Context context) {
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("Testowy tytuł")
                .setContentText("Testowa treść notyfikacji")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Testowa treść notyfikacji"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(END_OF_WORK_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, END_OF_WORK_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground);
    }
}
