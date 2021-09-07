package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner
import net.wojteksz128.worktimemeasureapp.util.livedata.ObservableLiveData

class DashboardViewModel(application: Application) : AndroidViewModel(application), ClassTagAware {
    val workTimeData = ObservableLiveData<WorkTimeData>()
    val workDay: LiveData<WorkDayEvents>
    val weekWorkDays: LiveData<List<WorkDayEvents>>
    val waitingFor = MutableLiveData(false)
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        val start = DateTimeProvider.weekBeginDay
        val end = DateTimeProvider.weekEndDay
        // TODO: 06.09.2021 init new work day at start and provide specified data based on current date
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
//        workDay = MutableLiveData(weekWorkDays.value?.maxByOrNull { it.workDay.beginSlot })
        workDay = workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
        weekWorkDays = workDayDao.findBetweenDates(start, end)
        workTimeData.value = WorkTimeData(start, end)
        this.secondRunner = PeriodicOperationRunner()
    }
}
