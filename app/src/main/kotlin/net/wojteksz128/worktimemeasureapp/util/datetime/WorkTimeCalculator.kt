package net.wojteksz128.worktimemeasureapp.util.datetime

import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.settings.Settings
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import javax.inject.Inject

class WorkTimeCalculator @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    @Suppress("PrivatePropertyName") private val Settings: Settings,
){

    fun calculateCurrentWorkTime(
        currentDay: WorkDay?,
        weekWorkDays: List<WorkDay>,
        weekRange: ClosedRange<LocalDate>,
    ): WorkTimeResult {
        val currentDayDate = prepareWorkDay(currentDay) ?: LocalDate.now()
        if (weekWorkDays.any { it.date !in weekRange })
            throw IllegalStateException("All week work days must be in week range.")

        if (currentDay != null && weekWorkDays.isNotEmpty() && weekWorkDays.let { currentDay !in it })
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

    private fun prepareWorkDay(workDay: WorkDay?): LocalDate? = workDay?.date

    private fun calculateCurrentWeekWorkTime(weekWorkDays: Collection<WorkDay>) =
        weekWorkDays.map { dateTimeUtils.mergeComeEventsDuration(it) }
            .fold(Duration.ZERO) { sum, element -> sum + element }

    // TODO: 30.08.2021 Obsługa określenia nierównomiernych dni roboczych
    private fun calculateWeekExpectedWorkTime(): Duration {
        val daysOfWorkingDaysNo = Settings.WorkTime.Week.DaysOfWorkingWeek.value.size.toLong()
        return Settings.WorkTime.Week.Duration.value.multipliedBy(daysOfWorkingDaysNo)
    }

    private fun calculateCurrentDayWorkTime(currentDay: WorkDay?): Duration {
        return currentDay?.let { dateTimeUtils.mergeComeEventsDuration(it) } ?: Duration.ZERO
    }

    // TODO: 30.08.2021 Obsługa określenia nierównomiernych dni roboczych
    private fun calculateCurrentDayExpectedWorkTime(): Duration {
        return Settings.WorkTime.Week.Duration.value
    }


    data class WorkTimeResult(
        val weekRange: ClosedRange<LocalDate>,
        val currentDay: LocalDate,
        val weekCurrentWorkTimeDuration: Duration,
        val weekExpectedWorkTimeDuration: Duration,
        val currentDayWorkTimeDuration: Duration,
        val currentDayExpectedWorkTimeDuration: Duration,
    ) {
        val weekRemainingWorkTimeDuration: Duration
            get() = weekExpectedWorkTimeDuration - weekCurrentWorkTimeDuration

        val currentDayRemainingWorkTimeDuration: Duration
            get() = currentDayExpectedWorkTimeDuration - currentDayWorkTimeDuration
    }
}