package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.EntityHistoryRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

@HiltViewModel
class WorkDayDetailsViewModel @Inject constructor(
    application: Application,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val entityHistoryRepository: EntityHistoryRepository,
) : AndroidViewModel(application), ClassTagAware {
    val workDay = MediatorLiveData<WorkDay>()

    val modifiedComeEventPosition = MutableLiveData<Int>()

    val history: LiveData<List<GroupedHistoryItem>> =
        workDay.switchMap { workDay ->
            workDay.id?.let { workDayId ->
                entityHistoryRepository.getGroupedHistory(workDayId).map { historyList ->
                    historyList.groupBy { it.changeGroupId }
                        .map { (_, group) ->
                            val first = group.first()
                            GroupedHistoryItem(
                                timestamp = first.timestamp,
                                entityType = first.entityType,
                                actionType = first.actionType,
                                changes = group.map {
                                    FieldChange(
                                        it.fieldName,
                                        it.oldValue,
                                        it.newValue
                                    )
                                }
                            )
                        }.sortedByDescending { it.timestamp }
                }
            }
        }

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

    fun onComeEventDelete(comeEvent: ComeEvent?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEvent?.let { comeEventRepository.delete(it) }
        }
    }

    fun onComeEventModified(modifiedComeEvent: ComeEvent) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEventRepository.save(modifiedComeEvent)
        }
    }
}