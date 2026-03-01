package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

open class WorkTimeNotificationFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dateTimeUtils: DateTimeUtils,
    private val dateTimeProvider: DateTimeProvider,
) {

    open fun createWorkInProgressNotification(
        workDay: WorkDay,
        workTimeBalance: WorkTimeBalance,
    ): WorkTimeInProgressNotification = WorkTimeInProgressNotification(
        context,
        workDay.date,
        workTimeBalance,
        dateTimeUtils
    )

    open fun createEndOfWorkNotification(
        standardEndTime: ZonedDateTime,
        balancedEndTime: ZonedDateTime,
    ): EndOfWorkTimeNotification =
        EndOfWorkTimeNotification(context, dateTimeProvider, standardEndTime, balancedEndTime)

    open fun createEndOfWorkNotification(): EndOfWorkTimeNotification {
        return EndOfWorkTimeNotification(context, dateTimeProvider)
    }
}