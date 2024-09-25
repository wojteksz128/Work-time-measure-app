package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
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
    private val comeEventRepository: ComeEventRepository,
    dateTimeProvider: DateTimeProvider,
    workTimeCalculator: WorkTimeCalculator,
) : AndroidViewModel(application), ClassTagAware {
    var workTimeCounterRunner: PeriodicOperation.PeriodicOperationRunner? = null
    val workDay: LiveData<WorkDay>
    val workTimeData = ObservableLiveData<WorkTimeData>()
    val waitingFor = MutableLiveData(false)
    var dayOffDialogShowed: Boolean = false
    private val weekWorkDays: LiveData<List<WorkDay>>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        val start = dateTimeProvider.weekBeginDay
        val end = dateTimeProvider.weekEndDay
        val currentTime = dateTimeProvider.currentTime
        val workTimeDataInst = WorkTimeData(start, end, workTimeCalculator)
        workTimeData.value = workTimeDataInst

        weekWorkDays = workDayRepository.getCurrentWeekWorkDaysInLiveData(start, end).apply {
            observeForever { workDays ->
                if (workDays != null) workTimeDataInst.weekWorkDays = workDays
            }
        }

        workDay = workDayRepository.getCurrentWorkDayInLiveData(currentTime).apply {
            observeForever { workDayEvents: WorkDay? ->
                workTimeDataInst.currentDay = workDayEvents
            }
        }.map {
            it ?: WorkDay(currentTime)
        }
    }

    fun onComeEventDelete(comeEvent: ComeEvent?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEvent?.let { comeEventRepository.delete(it) }
        }
    }

    fun onComeEventModified(modifiedComeEvent: ComeEvent) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEventRepository.save(modifiedComeEvent)
        }
    }
}

