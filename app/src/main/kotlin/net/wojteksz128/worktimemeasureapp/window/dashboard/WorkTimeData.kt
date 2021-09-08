package net.wojteksz128.worktimemeasureapp.window.dashboard

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeCalculator.calculateCurrentWorkTime
import org.threeten.bp.Duration
import java.util.*

class WorkTimeData(start: Date, end: Date): BaseObservable() {
    @Suppress("unused")
    val weekRange: ClosedRange<Date> = start..end

    var currentDay: WorkDayEvents? = null
        get
        set(value) {
            field = value
            updateData()
        }
    var weekWorkDays: List<WorkDayEvents> = listOf()
        get
        set(value) {
            field = value
            updateData()
        }

    var currentDayDate: Date? = null
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

    var remainingWeekWorkTime: Duration? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainingWeekWorkTime)
        }

    fun updateData() {
        val result = calculateCurrentWorkTime(currentDay, weekWorkDays, weekRange)
        currentDayDate = result.currentDay
        todayWorkTime = result.currentDayWorkTimeDuration
        remainingTodayWorkTime = result.currentDayRemainingWorkTimeDuration
        remainingWeekWorkTime = result.weekRemainingWorkTimeDuration
    }
}