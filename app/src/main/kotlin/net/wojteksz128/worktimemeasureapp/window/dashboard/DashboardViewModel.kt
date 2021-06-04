package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
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
        this.weekWorkDays = Pager(
            PagingConfig(
                20
            ),
            this.initialLoadKey,
            workDayDao.findBetweenDates(
                DateTimeProvider.weekBeginDay,
                DateTimeProvider.weekEndDay
            ).asPagingSourceFactory(Dispatchers.IO)
        ).liveData.build()
        this.secondRunner = PeriodicOperationRunner()
    }
}
