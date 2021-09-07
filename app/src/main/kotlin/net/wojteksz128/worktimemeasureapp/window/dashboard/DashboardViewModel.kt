package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner
import net.wojteksz128.worktimemeasureapp.util.livedata.ObservableLiveData

class DashboardViewModel(application: Application) : AndroidViewModel(application), ClassTagAware {
    val workTimeData = ObservableLiveData<WorkTimeData>()
    val waitingFor = MutableLiveData(false)
    val secondRunner: PeriodicOperationRunner<WorkDayEvents>

    init {
        Log.d(classTag, "ctor: Retrieve current work day with events")
        workTimeData.value =
            WorkTimeData(DateTimeProvider.weekBeginDay, DateTimeProvider.weekEndDay, application)
        this.secondRunner = PeriodicOperationRunner()
    }
}
