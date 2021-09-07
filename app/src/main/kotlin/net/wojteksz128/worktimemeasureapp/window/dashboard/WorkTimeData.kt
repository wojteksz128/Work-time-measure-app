package net.wojteksz128.worktimemeasureapp.window.dashboard

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import net.wojteksz128.worktimemeasureapp.BR
import java.util.*

class WorkTimeData(start: Date, end: Date): BaseObservable() {
    @Suppress("unused")
    val weekRange: ClosedRange<Date> = start..end

    var currentDayDate: String? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentDayDate)
        }

    var todayWorkTime: String? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.todayWorkTime)
        }

    var remainingTodayWorkTime: String? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainingTodayWorkTime)
        }

    var remainingWeekWorkTime: String? = null
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.remainingWeekWorkTime)
        }
}