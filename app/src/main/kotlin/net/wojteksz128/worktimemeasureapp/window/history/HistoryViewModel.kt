package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class HistoryViewModel(application: Application) : AndroidViewModel(application), ClassTagAware {

    val workDaysPager: Pager<Int, WorkDayEvents>

    init {
        Log.d(classTag, "ctor: Retrieve work days with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        val pagingConfig = PagingConfig(20, enablePlaceholders = true)
        val pagingSourceFactory =
            workDayDao.findAllInLiveData().asPagingSourceFactory(Dispatchers.IO)
        this.workDaysPager = Pager(pagingConfig, null, pagingSourceFactory)
    }
}
