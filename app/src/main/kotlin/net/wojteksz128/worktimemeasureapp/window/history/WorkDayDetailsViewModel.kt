package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class WorkDayDetailsViewModel @Inject constructor(
    application: Application,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository
) : AndroidViewModel(application), ClassTagAware {
    val workDay = MediatorLiveData<WorkDay>()

    val comeEventToDelete = MutableLiveData<ComeEvent>()
    val comeEventPosition = MutableLiveData<Int>()

    fun fillWorkDayUsingLocal(workDaySource: LiveData<WorkDay>) {
        workDay.addSource(workDaySource) {
            Log.d(classTag, "fillWorkDataFromLocalSource: Use variable source")
            workDay.value = it
        }
    }

    fun replaceWorkDayUsingRepository(
        workDayId: Long,
        previousWorkDaySourceSource: LiveData<WorkDay>
    ) {
        val workDayFromDB = workDayRepository.getWorkDayByIdInLiveData(workDayId)
        workDay.addSource(workDayFromDB) {
            Log.d(classTag, "fillUsingRepositoryWorkDay: Use database source")
            workDay.value = it
            workDay.removeSource(previousWorkDaySourceSource)
        }
    }

    fun onComeEventDelete() = viewModelScope.launch {
        Toast.makeText(getApplication(), "Delete", Toast.LENGTH_SHORT).show()
        withContext(Dispatchers.IO) {
            comeEventToDelete.value?.let { comeEventRepository.delete(it) }
        }
    }
}