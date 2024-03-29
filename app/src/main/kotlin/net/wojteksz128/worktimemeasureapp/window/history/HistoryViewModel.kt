package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Log
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
        this.workDays = LivePagedListBuilder(workDayDao.findAllInLiveData(), 20).build()
        this.secondRunner = PeriodicOperationRunner()
    }
}
