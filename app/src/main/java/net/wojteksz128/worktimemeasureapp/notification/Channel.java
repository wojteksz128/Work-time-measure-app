package net.wojteksz128.worktimemeasureapp.notification;

import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import net.wojteksz128.worktimemeasureapp.R;

public enum Channel {

    END_OF_WORK_CHANNEL("end-of-work-channel", R.string.channel_end_of_work_name, R.string.channel_end_of_work_description, 4/*NotificationManager.IMPORTANCE_HIGH*/);

    private final String id;
    private final int name;
    private final int description;
    private final int importance;


    Channel(String id, int name, int description, int importance) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.importance = importance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationChannel getNotificationChannel(Context context) {
        String name = context.getString(this.getName());
        String description = context.getString(this.getDescription());

        final NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
        notificationChannel.setDescription(description);

        return notificationChannel;
    }

    public String getId() {
        return id;
    }

    public int getName() {
        return name;
    }

    public int getDescription() {
        return description;
    }

    @SuppressWarnings("unused")
    public int getImportance() {
        return importance;
    }
}
