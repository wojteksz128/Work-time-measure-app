package net.wojteksz128.worktimemeasureapp.notification.endOfWork;

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
import android.util.Log;

import net.wojteksz128.worktimemeasureapp.R;
import net.wojteksz128.worktimemeasureapp.notification.Channel;
import net.wojteksz128.worktimemeasureapp.notification.NotificationAction;
import net.wojteksz128.worktimemeasureapp.window.main.MainActivity;

import java.text.MessageFormat;

import static net.wojteksz128.worktimemeasureapp.notification.Notification.END_OF_WORK;

public class EndOfWorkNotification {

    private static final String LOG = EndOfWorkNotification.class.getSimpleName();

    private static final int END_OF_WORK_NOTIFICATION_ID = END_OF_WORK.getNotificationId();
    private static final int END_OF_WORK_PENDING_INTENT_ID = END_OF_WORK.getPendingIntentId();

    private static final String CHANNEL_ID = Channel.END_OF_WORK_CHANNEL.getId();

    public static void createNotification(Context context) {
        Log.d(LOG, "createNotification: Create notification");
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_end_of_work_title))
                .setContentText(context.getString(R.string.notification_end_of_work_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_end_of_work_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(getAction(context, Action.END_OF_WORK_ACTION))
                .addAction(getAction(context, Action.IGNORE_REMINDER_ACTION))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            Log.d(LOG, "createNotification: Notification notifying");
            notificationManager.notify(END_OF_WORK_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private static NotificationCompat.Action getAction(Context context, Action action) {
        Log.v(LOG, MessageFormat.format("getAction: Create action {0}", action.name()));
        final Intent intent = new Intent(context, EndOfWorkNotificationIntentService.class);
        intent.setAction(action.name());
        final PendingIntent pendingIntent = PendingIntent.getService(context, action.getPendingIntentId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(action.getIcon(), context.getString(action.getTitle()), pendingIntent);
    }

    private static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, END_OF_WORK_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground);
    }

    public enum Action {
        END_OF_WORK_ACTION(new EndOfWorkAction(), 60, R.drawable.come_out_background, R.string.notification_end_of_work_action_come_out),
        IGNORE_REMINDER_ACTION(new IgnoreReminderAction(), 871, R.drawable.come_in_background, R.string.notification_end_of_work_action_ingore);

        private final NotificationAction notificationAction;
        private final int pendingIntentId;
        private final int icon;
        private final int title;

        Action(NotificationAction notificationAction, int pendingIntentId, int icon, int title) {

            this.notificationAction = notificationAction;
            this.pendingIntentId = pendingIntentId;
            this.icon = icon;
            this.title = title;
        }

        public void doAction(Context context) {
            this.notificationAction.invoke(context);
        }

        public int getPendingIntentId() {
            return pendingIntentId;
        }

        public int getIcon() {
            return icon;
        }

        public int getTitle() {
            return title;
        }
    }
}
