package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = DashboardViewModel::class.qualifiedName

    val workDay: LiveData<WorkDayEvents>
    val weekWorkDays: LiveData<List<WorkDayEvents>>
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(TAG, "ctor: Retrieve current work day with events")
        val workDayDao = AppDatabase.getInstance(application).workDayDao()
        this.workDay = workDayDao.findByIntervalContainsInLiveData(DateTimeProvider.currentTime)
        this.weekWorkDays = workDayDao.findBetweenDates(
            DateTimeProvider.weekBeginDay,
            DateTimeProvider.weekEndDay
        )
        this.secondRunner = PeriodicOperationRunner()
    }
}
