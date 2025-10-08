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
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WorkDayDetailsViewModel @Inject constructor(
    application: Application,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val entityHistoryRepository: EntityHistoryRepository,
    private val historyDisplayMapper: HistoryDisplayMapper,
    private val dayTimeUtils: DateTimeUtils,
    @Named("entryHistoryDateTimeFormat") private val dateTimeFormat: String,
) : AndroidViewModel(application), ClassTagAware {
    val workDay = MediatorLiveData<WorkDay>()

    val modifiedComeEventPosition = MutableLiveData<Int>()

    val history: LiveData<List<HistoryDisplayItem>> =
        workDay.switchMap { workDay ->
            workDay.id?.let {
                entityHistoryRepository.getGroupedHistoryForWorkDay(it).map { historyItems ->
                    historyItems.map { historyItem ->
                        HistoryDisplayItem(
                            timestamp = dayTimeUtils.formatDate(
                                dateTimeFormat,
                                historyItem.timestamp
                            ),
                            actionText = historyDisplayMapper.mapActionType(historyItem.actionType),
                            actionColorRes = historyDisplayMapper.mapActionToColor(historyItem.actionType),
                            entityText = historyDisplayMapper.mapEntityType(historyItem.entityType),
                            changes = historyItem.changes.map { change ->
                                ChangeDisplayItem(
                                    fieldName = change.fieldName,
                                    oldValue = change.oldValue,
                                    newValue = change.newValue
                                )
                            }
                        )
                    }
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