package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Log
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = DashboardViewModel::class.java.simpleName

    val workDay: LiveData<WorkDayEvents>
    val weekWorkDays: LiveData<PagedList<WorkDayEvents>>
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(TAG, "ctor: Retrieve current work day with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        this.workDay = workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
        this.weekWorkDays = LivePagedListBuilder(workDayDao.findBetweenDates(DateTimeProvider.weekBeginDay, DateTimeProvider.weekEndDay), 20).build()
        this.secondRunner = PeriodicOperationRunner()
    }
}
