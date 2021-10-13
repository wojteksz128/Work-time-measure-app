package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeCalculator
import net.wojteksz128.worktimemeasureapp.util.livedata.ObservableLiveData
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository,
    dateTimeProvider: DateTimeProvider,
    workTimeCalculator: WorkTimeCalculator
) : AndroidViewModel(application), ClassTagAware {
    var workTimeCounterRunner: PeriodicOperation.PeriodicOperationRunner? = null
    val workDay: LiveData<WorkDay>
    val workTimeData = ObservableLiveData<WorkTimeData>()
    val waitingFor = MutableLiveData(false)
    private val weekWorkDays: LiveData<List<WorkDay>>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        val start = dateTimeProvider.weekBeginDay
        val end = dateTimeProvider.weekEndDay
        val workTimeDataInst = WorkTimeData(start, end, workTimeCalculator)
        workTimeData.value = workTimeDataInst

        // TODO: 06.09.2021 init new work day at start and provide specified data based on current date
        weekWorkDays = workDayRepository.getCurrentWeekWorkDaysInLiveData(start, end).apply {
            observeForever { workDays ->
                if (workDays != null) workTimeDataInst.weekWorkDays = workDays
            }
        }

        workDay =
            workDayRepository.getCurrentWorkDayInLiveData(dateTimeProvider.currentTime).apply {
                observeForever { workDayEvents: WorkDay? ->
                    workTimeDataInst.currentDay = workDayEvents
                }
            }
    }
}

