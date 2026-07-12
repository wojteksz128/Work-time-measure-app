package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import javax.inject.Inject

@HiltViewModel
class WorkDaysHistoryViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository,
    private val comeEventRepository: ComeEventRepository,
    tickerFactory: TickerFactory,
) : AndroidViewModel(application), ClassTagAware {

    val workDaysPager: Flow<PagingData<WorkDayItemUiModel>> = Pager(
        config = PagingConfig(20, enablePlaceholders = true),
        pagingSourceFactory = workDayRepository.getAllPaged()
    ).flow
        .map { pagingData ->
            pagingData.map { workDay -> workDay.toUiModel(getApplication()) }
        }
        .cachedIn(viewModelScope)

    val ticker: SharedFlow<Unit> = tickerFactory.create(viewModelScope)

    init {
        Log.d(classTag, "ctor: Retrieve work days with events")
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
