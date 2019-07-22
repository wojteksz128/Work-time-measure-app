package net.wojteksz128.worktimemeasureapp.notification.base

enum class Notification(val notificationId: Int, val pendingIntentId: Int) {
    END_OF_WORK(251, 481),
    IN_WORK(252, 482);
}
