package net.wojteksz128.worktimemeasureapp.window.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val workDays: LiveData<List<WorkDayEvents>>
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(TAG, "ctor: Retrieve work days with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        this.workDays = workDayDao.findAllInLiveData()
        this.secondRunner = PeriodicOperationRunner()
    }

    companion object {

        private val TAG = MainViewModel::class.java.simpleName!!
    }
}
