package net.wojteksz128.worktimemeasureapp.util.datetime

import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.settings.Settings
import org.threeten.bp.Duration
import java.util.*

object WorkTimeCalculator {

    // TODO: 07.09.2021 Change way of realization work time counting
    fun calculateCurrentWorkTime(currentDay: WorkDayEvents?, weekWorkDays: List<WorkDayEvents>, weekRange: ClosedRange<Date>): WorkTimeResult {
        val currentDayDate = prepareWorkDay(currentDay) ?: Date()
        if (weekWorkDays.any { it.workDay.date !in weekRange })
            throw IllegalStateException("All week work days must be in week range.")

        if (weekWorkDays.isNotEmpty() && weekWorkDays.let { currentDay !in it })
            throw IllegalStateException("All week work days must be in week range.")
        else
            return WorkTimeResult(
                weekRange,
                currentDayDate,
                calculateCurrentWeekWorkTime(weekWorkDays).withNanos(0),
                calculateWeekExpectedWorkTime().withNanos(0),
                calculateCurrentDayWorkTime(currentDay).withNanos(0),
                calculateCurrentDayExpectedWorkTime().withNanos(0)
            )
    }

    private fun prepareWorkDay(workDay: WorkDayEvents?): Date? = workDay?.workDay?.date

    private fun calculateCurrentWeekWorkTime(weekWorkDays: Collection<WorkDayEvents>) =
        weekWorkDays.map { DateTimeUtils.mergeComeEventsDuration(it) }
            .fold(Duration.ZERO) { sum, element -> sum + element }

    // TODO: 30.08.2021 Obsługa określenia dni roboczych
    // TODO: 30.08.2021 Obsługa określenia nierównomiernych dni roboczych
    private fun calculateWeekExpectedWorkTime(): Duration {
        return Settings.WorkTime.Duration.value.multipliedBy(5)
    }

    private fun calculateCurrentDayWorkTime(currentDay: WorkDayEvents?): Duration {
        return currentDay?.let { DateTimeUtils.mergeComeEventsDuration(it) } ?: Duration.ZERO
    }

    // TODO: 30.08.2021 Obsługa określenia nierównomiernych dni roboczych
    private fun calculateCurrentDayExpectedWorkTime(): Duration {
        return Settings.WorkTime.Duration.value
    }


    data class WorkTimeResult(
        val weekRange: ClosedRange<Date>,
        val currentDay: Date,
        val weekCurrentWorkTimeDuration: Duration,
        val weekExpectedWorkTimeDuration: Duration,
        val currentDayWorkTimeDuration: Duration,
        val currentDayExpectedWorkTimeDuration: Duration
    ) {
        val weekRemainingWorkTimeDuration: Duration
            get() = weekExpectedWorkTimeDuration - weekCurrentWorkTimeDuration

        val currentDayRemainingWorkTimeDuration: Duration
            get() = currentDayExpectedWorkTimeDuration - currentDayWorkTimeDuration
    }
}