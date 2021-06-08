package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = HistoryViewModel::class.java.simpleName

    val workDaysPager: Pager<Int, WorkDayEvents>
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(TAG, "ctor: Retrieve work days with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        val pagingConfig = PagingConfig(20, enablePlaceholders = true)
        val pagingSourceFactory =
            workDayDao.findAllInLiveData().asPagingSourceFactory(Dispatchers.IO)
        this.workDaysPager = Pager(pagingConfig, null, pagingSourceFactory)
        this.secondRunner = PeriodicOperationRunner()
    }
}
