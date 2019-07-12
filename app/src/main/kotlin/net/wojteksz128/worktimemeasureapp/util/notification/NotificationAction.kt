package net.wojteksz128.worktimemeasureapp.util.notification

import android.content.Context

interface NotificationAction {

    operator fun invoke(context: Context)
}
