package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject

@HiltViewModel
class WorkDayDetailsViewModel @Inject constructor(
    application: Application,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val dateTimeUtils: DateTimeUtils
) : AndroidViewModel(application), ClassTagAware {
    val workDay = MediatorLiveData<WorkDay>()

    val comeEventToDelete = MutableLiveData<ComeEvent>()
    val comeEventPosition = MutableLiveData<Int>()

    fun fillWorkDayUsingLocal(workDaySource: LiveData<WorkDay>) {
        workDay.addSource(workDaySource) {
            Log.d(classTag, "fillWorkDayUsingLocal: Use variable source")
            workDay.value = it
        }
    }

    fun replaceWorkDayUsingRepository(
        workDayId: Long,
        previousWorkDaySourceSource: LiveData<WorkDay>
    ) {
        val workDayFromDB = workDayRepository.getWorkDayByIdInLiveData(workDayId)
        workDay.addSource(workDayFromDB) {
            Log.d(classTag, "replaceWorkDayUsingRepository: Use database source")
            workDay.value = it
            workDay.removeSource(previousWorkDaySourceSource)
        }
    }

    fun prepareDeleteMessage(comeEvent: ComeEvent): String {
        return getApplication<WorkTimeMeasureApp>().getString(
            R.string.work_day_details_come_events_action_delete_message,
            dateTimeUtils.formatDate(
                getApplication<WorkTimeMeasureApp>().getString(R.string.history_day_event_time_format),
                comeEvent.startDate
            ),
            dateTimeUtils.formatDate(
                getApplication<WorkTimeMeasureApp>().getString(R.string.history_day_event_time_format),
                comeEvent.endDate
            )
        )
    }

    fun onComeEventDelete() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEventToDelete.value?.let { comeEventRepository.delete(it) }
        }
    }
}