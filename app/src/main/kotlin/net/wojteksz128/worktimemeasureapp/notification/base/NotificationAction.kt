package net.wojteksz128.worktimemeasureapp.notification.base

import android.content.Context

interface NotificationAction {

    operator fun invoke(context: Context)
}
