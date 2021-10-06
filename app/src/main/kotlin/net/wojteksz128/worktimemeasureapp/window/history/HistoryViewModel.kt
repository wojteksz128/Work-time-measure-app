package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val workDayWithEventsMapper: WorkDayWithEventsMapper
) : AndroidViewModel(application), ClassTagAware {

    val workDaysPager: Pager<Int, WorkDay>

    init {
        Log.d(classTag, "ctor: Retrieve work days with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        val pagingConfig = PagingConfig(20, enablePlaceholders = true)
        val pagingSourceFactory = workDayDao.findAllInLiveData()
            .map { workDayWithEventsMapper.mapToDomainModel(it) }
            .asPagingSourceFactory(Dispatchers.IO)
        this.workDaysPager = Pager(pagingConfig, null, pagingSourceFactory)
    }
}
