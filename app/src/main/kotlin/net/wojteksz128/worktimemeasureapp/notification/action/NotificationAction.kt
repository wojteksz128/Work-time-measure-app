package net.wojteksz128.worktimemeasureapp.notification.action

import android.content.Context

interface NotificationAction {

    operator fun invoke(context: Context)
}
