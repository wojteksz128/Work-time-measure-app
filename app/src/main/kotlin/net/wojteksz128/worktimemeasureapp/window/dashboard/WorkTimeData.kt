package net.wojteksz128.worktimemeasureapp.window.dashboard

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeCalculator
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class WorkTimeData(
    start: LocalDate,
    end: LocalDate,
    private val workTimeCalculator: WorkTimeCalculator,
    private val dateTimeProvider: DateTimeProvider,
) :
    BaseObservable() {
    private val weekRange: ClosedRange<LocalDate> = start..end

    @Suppress("RedundantGetter")
    var currentDay: WorkDay? = null
        get
        set(value) {
            field = value
            updateData()
        }
    @Suppress("RedundantGetter")
    var weekWorkDays: List<WorkDay> = listOf()
        get
        set(value) {
            field = value
            updateData()
        }

    var currentDayDate: LocalDate? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentDayDate)
        }

    var todayWorkTime: Duration? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.todayWorkTime)
        }

    var remainingTodayWorkTime: Duration? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainingTodayWorkTime)
        }

    var expectedEndWorkDayTime: ZonedDateTime? = null
        @Bindable get

    var remainingWeekWorkTime: Duration? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainingWeekWorkTime)
        }

    fun updateData() {
        val result = workTimeCalculator.calculateCurrentWorkTime(currentDay, weekWorkDays, weekRange)
        currentDayDate = result.currentDay
        todayWorkTime = result.currentDayWorkTimeDuration
        remainingTodayWorkTime = result.currentDayRemainingWorkTimeDuration
        expectedEndWorkDayTime = dateTimeProvider.currentTime.plus(remainingTodayWorkTime)
        remainingWeekWorkTime = result.weekRemainingWorkTimeDuration
    }
}