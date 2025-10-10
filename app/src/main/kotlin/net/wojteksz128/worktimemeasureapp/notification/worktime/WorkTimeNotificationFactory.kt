package net.wojteksz128.worktimemeasureapp.notification.worktime

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import javax.inject.Inject

class WorkTimeNotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dateTimeUtils: DateTimeUtils,
) {

    fun createWorkInProgressNotification(
        workDay: WorkDay,
        workTimeBalance: WorkTimeBalance,
    ): WorkTimeInProgressNotification {
        val startTime = workDay.events.lastOrNull()?.startDate
            ?: throw IllegalStateException("Cannot create notification for work day without start time")

        val standardEndTime = startTime.plus(workTimeBalance.remainingTodayWorkTime)
        val balancedEndTime = standardEndTime.plus(workTimeBalance.monthlyBalance)

        return WorkTimeInProgressNotification(
            context,
            workDay.date,
            standardEndTime,
            balancedEndTime,
            dateTimeUtils
        )
    }

    fun createEndOfWorkNotification(): EndOfWorkTimeNotification {
        return EndOfWorkTimeNotification(context)
    }
}