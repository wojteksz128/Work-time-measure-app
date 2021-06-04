package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = HistoryViewModel::class.java.simpleName

    val workDays: LiveData<PagedList<WorkDayEvents>>
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(TAG, "ctor: Retrieve work days with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        this.workDays = Pager(
            PagingConfig(20),
            this.initialLoadKey,
            workDayDao.findAllInLiveData().asPagingSourceFactory(Dispatchers.IO)
        ).liveData.build()
        this.secondRunner = PeriodicOperationRunner()
    }
}
