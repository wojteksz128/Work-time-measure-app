package net.wojteksz128.worktimemeasureapp.service

import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkTimeRequirements
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.LocalDateRange
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import javax.inject.Inject

class WorkTimeRequirementsCalculator @Inject constructor(
    private val workDayRepository: WorkDayRepository,
    private val dateTimeUtils: DateTimeUtils,
    private val dayOffService: DayOffService,
    @Suppress("PrivatePropertyName") private val Settings: Settings,
) {

    suspend fun calculate(workDay: WorkDay): WorkTimeRequirements {
        val requiredToday = calculateRequiredWorkTimeForWorkDay(workDay)
        val balanceBeforeToday = getBalanceOfMonthBeforeDate(workDay.date)

        return WorkTimeRequirements(
            requiredToday,
            balanceBeforeToday
        )
    }

    private suspend fun calculateRequiredWorkTimeForWorkDay(workDay: WorkDay): Duration =
        dayOffService.getDayType(workDay.date).takeIf { it == DayType.WorkDay }?.let {
            Settings.WorkTime.Week.Duration.value
        } ?: Duration.ZERO

    private suspend fun getBalanceOfMonthBeforeDate(date: LocalDate): Duration {
        val daysInMonthRange = dateTimeUtils.getDaysInMonthRangeToDate(date.minusDays(1))
        return calculateBalanceForDays(daysInMonthRange)
    }

    private suspend fun calculateBalanceForDays(dateRange: LocalDateRange): Duration {
        val totalWorked =
            workDayRepository.getWorkDaysForRange(dateRange).fold(Duration.ZERO) { acc, workDay ->
                acc + dateTimeUtils.mergeComeEventsDuration(workDay)
            }
        val totalRequired = calculateExpectedWorkTime(dateRange)
        return totalWorked - totalRequired
    }

    private suspend fun calculateExpectedWorkTime(dateRange: LocalDateRange): Duration =
        dateRange.filter { dayOffService.getDayType(it) == DayType.WorkDay }
            .fold(Duration.ZERO) { acc, _ ->
                acc + Settings.WorkTime.Week.Duration.value
            }
}

