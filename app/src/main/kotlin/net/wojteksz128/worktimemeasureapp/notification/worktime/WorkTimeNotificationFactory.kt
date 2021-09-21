package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import java.util.*

object WorkTimeNotificationFactory {

    fun createWorkTimeInProgressNotification(context: Context, endOfWorkTime: Date) =
        WorkTimeInProgressNotification(context, endOfWorkTime)

    fun createEndOfWorkTimeNotification(context: Context) = EndOfWorkTimeNotification(context)
}