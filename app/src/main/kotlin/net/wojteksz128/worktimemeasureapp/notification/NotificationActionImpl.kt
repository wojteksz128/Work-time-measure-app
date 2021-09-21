package net.wojteksz128.worktimemeasureapp.notification

import android.content.Context

interface NotificationActionImpl {

    operator fun invoke(context: Context)
}
