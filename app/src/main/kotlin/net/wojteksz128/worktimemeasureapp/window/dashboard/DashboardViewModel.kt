package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
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
    val workDay: LiveData<WorkDay>
    val workTimeData = ObservableLiveData<WorkTimeData>()

    val liveWorkTimeData: LiveData<WorkTimeData>

    val ticker: SharedFlow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000))

    val waitingFor = MutableLiveData(false)
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
        liveWorkTimeData = workDay.switchMap { currentWorkDay ->
            val isTimerActive = currentWorkDay.events.any { !it.isEnded }

            if (isTimerActive) liveData {
                while (true) {
                    val updatedData = workTimeData.value?.apply { updateData() }
                    if (updatedData != null)
                        emit(updatedData)
                    delay(1000)
                }
            } else {
                val updatedData = workTimeData.value!!.apply { updateData() }
                MutableLiveData(updatedData)
            }
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

