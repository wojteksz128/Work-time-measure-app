package net.wojteksz128.worktimemeasureapp.util.datetime

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.settings.Settings
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class WorkTimeBalanceCalculator @Inject constructor(
    private val workDayRepository: WorkDayRepository,
    private val dateTimeUtils: DateTimeUtils,
    private val dayOffService: DayOffService,
    @Suppress("PrivatePropertyName") private val Settings: Settings,
) {

    suspend fun calculateBalanceForWorkDay(workDay: WorkDay): WorkTimeBalance {
        val todayWorkTime = calculateWorkTimeForWorkDay(workDay)
        val requiredToday = calculateRequiredWorkTimeForWorkDay(workDay)
        val balanceBeforeToday = getBalanceOfMonthBeforeDate(workDay.date)

        return WorkTimeBalance(
            todayWorkTime,
            requiredToday,
            balanceBeforeToday
        )
    }

    fun updateTodayBalance(workDay: WorkDay, previousBalance: WorkTimeBalance): WorkTimeBalance {
        val todayWorkTime = calculateWorkTimeForWorkDay(workDay)

        return WorkTimeBalance(
            todayWorkTime,
            previousBalance.standardRequiredToday,
            previousBalance.monthlyBalance
        )
    }

    private fun calculateWorkTimeForWorkDay(workDay: WorkDay): Duration =
        dateTimeUtils.mergeComeEventsDuration(workDay)

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

@Parcelize
data class WorkTimeBalance(
    val todayWorkTime: Duration,
    val standardRequiredToday: Duration,
    val monthlyBalance: Duration,
) : Parcelable {

    val balancedRequiredToday: Duration
        get() = standardRequiredToday - monthlyBalance

    val standardRemainingToday: Duration
        get() = standardRequiredToday - todayWorkTime

    val standardEndTime: ZonedDateTime
        get() = ZonedDateTime.now() + standardRemainingToday

    val balancedEndTime: ZonedDateTime
        get() = ZonedDateTime.now() + standardRemainingToday - monthlyBalance
}