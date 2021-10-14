package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class WorkDayDetailsViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository
) : AndroidViewModel(application), ClassTagAware {
    private val _workDayId: MutableLiveData<Long> = MutableLiveData()
    private val _workDay: MutableLiveData<WorkDay> = MutableLiveData()
    private val _repositoryWorkDayResultObserver = Observer<WorkDay> {
        _workDay.value = it
    }
    private var _repositoryWorkDay: LiveData<WorkDay>? = null

    val workDay: LiveData<WorkDay>
        get() = _workDay

    var workDayId: Long?
        get() = _workDayId.value
        set(value) {
            Log.d(classTag, "setWorkDayId: Old workDayId = $workDayId, new workDayId = $value")
            _workDayId.value = value
        }

    init {
        // TODO: 14.10.2021 Probably on this place is problem, change to MVI DataState
        _workDayId.observeForever { workDayId ->
            Log.d(classTag, "observeForever: Replace _repositoryWorkDay")
            _repositoryWorkDay?.removeObserver(_repositoryWorkDayResultObserver)
            _repositoryWorkDay = workDayRepository.getWorkDayByIdInLiveData(workDayId)
            _repositoryWorkDay?.observeForever(_repositoryWorkDayResultObserver)
        }
    }
}