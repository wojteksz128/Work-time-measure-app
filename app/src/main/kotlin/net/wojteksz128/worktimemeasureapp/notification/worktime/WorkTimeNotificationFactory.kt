package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

open class WorkTimeNotificationFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
) {

    open fun createWorkInProgressNotification(inProgressState: WorkState.InProgress) =
        WorkTimeInProgressNotification(context, inProgressState)

    open fun createEndOfWorkNotification(
        standardEndTime: ZonedDateTime,
        balancedEndTime: ZonedDateTime,
    ): EndOfWorkTimeNotification =
        EndOfWorkTimeNotification(context, dateTimeProvider, standardEndTime, balancedEndTime)

    open fun createEndOfWorkNotification() = EndOfWorkTimeNotification(context, dateTimeProvider)
}