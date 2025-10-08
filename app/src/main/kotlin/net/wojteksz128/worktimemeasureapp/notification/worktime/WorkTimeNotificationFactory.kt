package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import org.threeten.bp.ZonedDateTime

object WorkTimeNotificationFactory {

    fun createWorkTimeInProgressNotification(
        context: Context,
        endOfWorkTime: ZonedDateTime,
        dateTimeUtils: DateTimeUtils,
    ) = WorkTimeInProgressNotification(context, endOfWorkTime, dateTimeUtils)

    fun createEndOfWorkTimeNotification(context: Context) = EndOfWorkTimeNotification(context)
}