package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalanceCalculator
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository,
    private val comeEventRepository: ComeEventRepository,
    dateTimeProvider: DateTimeProvider,
    private val workTimeBalanceCalculator: WorkTimeBalanceCalculator,
    tickerFactory: TickerFactory,
) : AndroidViewModel(application), ClassTagAware {
    val workDay: LiveData<WorkDay> =
        workDayRepository.getWorkDayByDateInLiveData(dateTimeProvider.currentDate)

    val workTimeBalance: LiveData<WorkTimeBalance> = workDay.switchMap { workDay ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay))
            ticker.collect {
                emit(workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay))
            }
        }
    }

    val ticker: SharedFlow<Unit> = tickerFactory.create(viewModelScope)

    val waitingFor = MutableLiveData(false)

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

