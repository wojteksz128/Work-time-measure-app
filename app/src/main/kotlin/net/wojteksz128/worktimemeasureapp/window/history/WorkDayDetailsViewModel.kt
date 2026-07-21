package net.wojteksz128.worktimemeasureapp.window.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.EntityHistoryRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.service.WorkTimeRequirementsCalculator
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.formatToString
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import net.wojteksz128.worktimemeasureapp.util.model.extension.workTime
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WorkDayDetailsViewModel @Inject constructor(
    application: Application,
    private val comeEventRepository: ComeEventRepository,
    private val workDayRepository: WorkDayRepository,
    private val entityHistoryRepository: EntityHistoryRepository,
    private val historyDisplayMapper: HistoryDisplayMapper,
    private val workTimeRequirementsCalculator: WorkTimeRequirementsCalculator,
    tickerFactory: TickerFactory,
    workStateFlow: StateFlow<@JvmSuppressWildcards WorkState>,
    @param:Named("entryHistoryDateTimeFormat") private val dateTimeFormat: String,
) : AndroidViewModel(application), ClassTagAware {

    val ticker = tickerFactory.create(viewModelScope)

    private val workDay = MediatorLiveData<WorkDay>()
    private val activeWorkState = workStateFlow.asLiveData()
    private val pastDayExpectedDuration = MutableLiveData<Duration>()
    private val tickerLiveData = ticker.asLiveData()

    private val history = workDay.switchMap { workDay ->
            workDay.id?.let {
                entityHistoryRepository.getGroupedHistoryForWorkDay(it).map { historyItems ->
                    historyItems.map { historyItem ->
                        HistoryDisplayItem(
                            timestamp = historyItem.timestamp.formatToString(dateTimeFormat),
                            actionText = historyDisplayMapper.mapActionType(historyItem.actionType),
                            actionColorRes = historyDisplayMapper.mapActionToColor(historyItem.actionType),
                            entityText = historyDisplayMapper.mapEntityType(historyItem.entityType),
                            changes = historyItem.changes.map { change ->
                                ChangeDisplayItem(
                                    fieldName = historyDisplayMapper.mapFieldName(change.fieldName),
                                    oldValue = change.oldValue,
                                    newValue = change.newValue
                                )
                            }
                        )
                    }
                }
            } ?: MediatorLiveData<List<HistoryDisplayItem>>().apply { value = emptyList() }
        }

    private val _uiModel = MediatorLiveData(WorkDayDetailsUiModel())
    val uiModel: LiveData<WorkDayDetailsUiModel> = _uiModel

    init {
        _uiModel.addSource(workDay) { day ->
            calculatePastRequirementsIfNeeded(day)
            updateUiState(day, history.value, activeWorkState.value, pastDayExpectedDuration.value)
        }
        _uiModel.addSource(history) { hist ->
            updateUiState(workDay.value, hist, activeWorkState.value, pastDayExpectedDuration.value)
        }
        _uiModel.addSource(activeWorkState) { state ->
            updateUiState(workDay.value, history.value, state, pastDayExpectedDuration.value)
        }
        _uiModel.addSource(pastDayExpectedDuration) { pastExpectedDuration ->
            updateUiState(workDay.value, history.value, activeWorkState.value, pastExpectedDuration)
        }
        _uiModel.addSource(tickerLiveData) {
            updateUiState(
                workDay.value,
                history.value,
                activeWorkState.value,
                pastDayExpectedDuration.value
            )
        }
    }

    private fun calculatePastRequirementsIfNeeded(day: WorkDay?) {
        if (day == null) return
        viewModelScope.launch {
            val requirements = workTimeRequirementsCalculator.calculate(day)
            pastDayExpectedDuration.value = requirements.standardRequiredToday
        }
    }

    private fun updateUiState(
        day: WorkDay?,
        hist: List<HistoryDisplayItem>?,
        state: WorkState?,
        pastExpectedDuration: Duration?,
    ) {
        if (day == null) return

        val events = day.events
        val uiEvents = events.map { it.toUiModel(getApplication()) }
        val historyList = hist ?: emptyList()

        val isToday = state is WorkState.Loaded && state.workDay.date == day.date

        val durationText =
            if (isToday) (state as WorkState.Loaded).todayWorkTime.toCounterString()
            else day.workTime.toCounterString()

        val expectedDurationText =
            if (isToday) (state as WorkState.Loaded).workTimeRequirements.standardRequiredToday.toCounterString()
            else (pastExpectedDuration ?: Duration.ZERO).toCounterString()

        _uiModel.value = WorkDayDetailsUiModel(
            yearAndMonth = day.date.formatToString("LLLL yyyy"),
            day = day.date.formatToString("d"),
            dayOfWeek = day.date.formatToString("EEEE"),
            durationText = durationText,
            expectedDurationText = expectedDurationText,
            comeEvents = uiEvents,
            historyItems = historyList,
            isEventsListVisible = events.isNotEmpty(),
            isNoEventsLabelVisible = events.isEmpty(),
            isHistoryListVisible = historyList.isNotEmpty(),
            isNoHistoryLabelVisible = historyList.isEmpty()
        )
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