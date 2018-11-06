package net.wojteksz128.worktimemeasureapp.notification;

public enum Notification {

    END_OF_WORK (251, 481);

    private final int notificationId;
    private final int pendingIntentId;

    Notification(int notificationId, int pendingIntentId) {

        this.notificationId = notificationId;
        this.pendingIntentId = pendingIntentId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getPendingIntentId() {
        return pendingIntentId;
    }
}
