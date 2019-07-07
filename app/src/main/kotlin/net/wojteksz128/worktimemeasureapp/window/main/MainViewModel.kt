package net.wojteksz128.worktimemeasureapp.window.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Log
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val workDays: LiveData<PagedList<WorkDayEvents>>
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(TAG, "ctor: Retrieve work days with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        this.workDays = LivePagedListBuilder(workDayDao.findAllInLiveData(), 20).build()
        this.secondRunner = PeriodicOperationRunner()
    }

    companion object {

        private val TAG = MainViewModel::class.java.simpleName!!
    }
}
