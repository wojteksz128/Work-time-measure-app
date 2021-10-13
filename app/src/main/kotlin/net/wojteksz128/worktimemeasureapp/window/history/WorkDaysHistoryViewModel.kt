package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class WorkDaysHistoryViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository
) : AndroidViewModel(application), ClassTagAware {

    val workDaysPager: Pager<Int, WorkDay>

    init {
        Log.d(classTag, "ctor: Retrieve work days with events")
        val pagingConfig = PagingConfig(20, enablePlaceholders = true)
        val pagingSourceFactory = workDayRepository.getAllPaged()
        this.workDaysPager = Pager(pagingConfig, null, pagingSourceFactory)
    }
}
