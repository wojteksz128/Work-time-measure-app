package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.WorkTimeTimer
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.livedata.ObservableLiveData
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ItemUpdate

class DashboardViewModel(application: Application) : AndroidViewModel(application), ClassTagAware {
    var workTimeCounterRunner: WorkTimeTimer.WorkTimeTimerRunner? = null
    val workDay: LiveData<WorkDayEvents>
    val notEndedEventsIndex = mutableListOf<ItemUpdate>()
    val workTimeData = ObservableLiveData<WorkTimeData>()
    val waitingFor = MutableLiveData(false)
    private val weekWorkDays: LiveData<List<WorkDayEvents>>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        val start = DateTimeProvider.weekBeginDay
        val end = DateTimeProvider.weekEndDay
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        val workTimeDataInst = WorkTimeData(start, end)
        workTimeData.value = workTimeDataInst

        // TODO: 06.09.2021 init new work day at start and provide specified data based on current date
        weekWorkDays = workDayDao.findBetweenDates(start, end)
        weekWorkDays.observeForever { workDays ->
            if (workDays != null) workTimeDataInst.weekWorkDays = workDays
        }

        workDay = workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
        workDay.observeForever { workDayEvents: WorkDayEvents? ->
            workTimeDataInst.currentDay = workDayEvents
        }
    }
}

