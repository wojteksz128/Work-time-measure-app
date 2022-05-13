package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class WorkDaysHistoryViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository,
    private val comeEventRepository: ComeEventRepository
) : AndroidViewModel(application), ClassTagAware {
    private val workDayItemsViewModels = mutableMapOf<Long, WorkDayAdapter.WorkDayItemViewModel>()
    val workDaysPager: Pager<Int, WorkDay>

    init {
        Log.d(classTag, "ctor: Retrieve work days with events")
        val pagingConfig = PagingConfig(20, enablePlaceholders = true)
        val pagingSourceFactory = workDayRepository.getAllPaged()
        this.workDaysPager = Pager(pagingConfig, null, pagingSourceFactory)
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

    fun getWorkDayItemViewModel(workDay: WorkDay): WorkDayAdapter.WorkDayItemViewModel {
        if (workDay.id == null)
            throw IllegalStateException("WorkDay without DB id cannot have WorkDayItemViewModel.")

        if (!workDayItemsViewModels.containsKey(workDay.id))
            workDayItemsViewModels[workDay.id] = WorkDayAdapter.WorkDayItemViewModel()

        return workDayItemsViewModels[workDay.id]!!
    }
}
