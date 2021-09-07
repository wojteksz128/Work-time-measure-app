package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import java.util.*

class WorkTimeData(start: Date, end: Date, application: Application): BaseObservable() {
    @Suppress("unused")
    val weekRange: ClosedRange<Date> = start..end
    val workDay: LiveData<WorkDayEvents>
    val weekWorkDays: LiveData<List<WorkDayEvents>>

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

    init {
        // TODO: 06.09.2021 init new work day at start and provide specified data based on current date
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        workDay = workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
        weekWorkDays = workDayDao.findBetweenDates(start, end)
    }
}