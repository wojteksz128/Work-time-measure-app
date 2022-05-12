package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import java.util.*

object WorkTimeNotificationFactory {

    fun createWorkTimeInProgressNotification(
        context: Context,
        endOfWorkTime: Date,
        dateTimeUtils: DateTimeUtils
    ) = WorkTimeInProgressNotification(context, endOfWorkTime, dateTimeUtils)

    fun createEndOfWorkTimeNotification(context: Context) = EndOfWorkTimeNotification(context)
}