package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class SelectedWorkDayViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), ClassTagAware {
    val selected = MutableLiveData<WorkDay>()

    fun select(workDay: WorkDay) {
        Log.d(
            classTag, "select: Selection changed\n" +
                    "\tOld selectedWorkDay = ${selected.value}\n" +
                    "\tnew selectedWorkDay = $workDay"
        )
        selected.value = workDay
    }
}
