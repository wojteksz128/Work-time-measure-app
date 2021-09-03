package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class DashboardViewModel(application: Application) : AndroidViewModel(application), ClassTagAware {
    // TODO: 03.09.2021 change to custom livedata or flow
    val currentDayDate = MutableLiveData<String>()
    // TODO: 03.09.2021 change to custom livedata or flow
    val todayWorkTime = MutableLiveData<String>()
    // TODO: 03.09.2021 change to custom livedata or flow
    val remainingTodayWorkTime = MutableLiveData<String>()
    // TODO: 03.09.2021 change to custom livedata or flow
    val remainingWeekWorkTime = MutableLiveData<String>()
    val workDay: LiveData<WorkDayEvents>
    val weekWorkDays: LiveData<List<WorkDayEvents>>
    val waitingFor = MutableLiveData(false)
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        this.workDay = workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
        this.weekWorkDays = workDayDao.findBetweenDates(
            DateTimeProvider.weekBeginDay,
            DateTimeProvider.weekEndDay
        )
        this.secondRunner = PeriodicOperationRunner()
    }
}
