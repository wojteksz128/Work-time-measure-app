package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.livedata.ObservableLiveData
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    private val workDayWithEventsMapper: WorkDayWithEventsMapper
) : AndroidViewModel(application), ClassTagAware {
    var workTimeCounterRunner: PeriodicOperation.PeriodicOperationRunner? = null
    val workDay: LiveData<WorkDay>
    val workTimeData = ObservableLiveData<WorkTimeData>()
    val waitingFor = MutableLiveData(false)
    private val weekWorkDays: LiveData<List<WorkDay>>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        val start = DateTimeProvider.weekBeginDay
        val end = DateTimeProvider.weekEndDay
        val workTimeDataInst = WorkTimeData(start, end)
        workTimeData.value = workTimeDataInst

        // TODO: 06.09.2021 init new work day at start and provide specified data based on current date
        weekWorkDays = getWeekWorkDays(start, end)
        weekWorkDays.observeForever { workDays ->
            if (workDays != null) workTimeDataInst.weekWorkDays = workDays
        }

        workDay = getCurrentWorkDay()
        workDay.observeForever { workDayEvents: WorkDay? ->
            workTimeDataInst.currentDay = workDayEvents
        }
    }

    // TODO: 06.10.2021 Move to repository
    private fun getWeekWorkDays(start: Date, end: Date): LiveData<List<WorkDay>> {
        val workDayDao = AppDatabase.getInstance(getApplication()).workDayDao()
        val workDays = workDayDao.findBetweenDates(start, end)

        return workDays.map { workDayWithEventsMapper.mapToDomainModelList(it) }
    }

    // TODO: 06.10.2021 Move to repository
    private fun getCurrentWorkDay(): LiveData<WorkDay> {
        val workDayDao = AppDatabase.getInstance(getApplication()).workDayDao()
        return workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
            .map { workDayWithEventsDto -> workDayWithEventsDto?.let { workDayWithEventsMapper.mapToDomainModel(it) } }
    }
}

