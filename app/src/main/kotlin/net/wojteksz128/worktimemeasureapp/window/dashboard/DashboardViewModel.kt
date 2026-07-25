package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.comeevent.NewEventRegisterListener
import net.wojteksz128.worktimemeasureapp.util.datetime.formatToString
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import net.wojteksz128.worktimemeasureapp.window.history.toUiModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    workStateFlow: StateFlow<@JvmSuppressWildcards WorkState>,
    private val comeEventRepository: ComeEventRepository,
    private val notificationService: WorkTimeNotificationService,
    private val comeEventUtils: ComeEventUtils,
) : AndroidViewModel(application), NewEventRegisterListener, ClassTagAware {

    private val _uiModel = MediatorLiveData(DashboardUiState(isLoading = true))
    val uiState: LiveData<DashboardUiState> = _uiModel

    private val mSnackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = mSnackbarMessage

    val ticker: SharedFlow<Unit> = workStateFlow.drop(1).map { }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        replay = 0
    )

    val waitingFor = MutableLiveData(false)

    private var wasWorkFinished: Boolean? = null

    init {
        _uiModel.addSource(workStateFlow.asLiveData(viewModelScope.coroutineContext)) { workState ->
            updateUiState(workState, waitingFor.value == true)
        }
        _uiModel.addSource(waitingFor) { isWaiting ->
            updateUiState(workStateFlow.value, isWaiting)
        }

        viewModelScope.launch {
            workStateFlow.collect { workState ->
                handleServiceAndNotifications(workState)
            }
        }
    }

    private fun updateUiState(state: WorkState, isWaiting: Boolean) {
        _uiModel.value = when (state) {
            is WorkState.Loading -> DashboardUiState(isLoading = true)
            is WorkState.Loaded -> {
                val currentDateFormat =
                    getApplication<Application>().getString(R.string.history_work_day_label_format)
                DashboardUiState(
                    standardRemainingTodayText = state.standardWorkTime.remainingWorkTime.toCounterString(),
                    monthlyBalanceText = state.workTimeRequirements.monthlyBalance.toCounterString(),
                    todayWorkTimeText = state.todayWorkTime.toCounterString(),
                    currentDayLabel = state.workDay.date.formatToString(currentDateFormat),
                    isEventsListVisible = state.workDay.events.isNotEmpty(),
                    isNoEventsLabelVisible = state.workDay.events.isEmpty(),
                    isLoading = isWaiting,
                    comeEvents = state.workDay.events.map { it.toUiModel(getApplication()) }
                )
            }
        }
    }

    private fun handleServiceAndNotifications(workState: WorkState) {
        val isWorkFinished = workState !is WorkState.InProgress

        if (isWorkFinished == wasWorkFinished) return

        if (isWorkFinished) {
            stopTrackingService()
            notificationService.cancelEndOfWorkNotification()
        } else {
            startTrackingService()
            if (workState is WorkState.InProgress) {
                notificationService.scheduleEndOfWorkNotification(workState)
            }
        }

        wasWorkFinished = isWorkFinished
    }

    private fun startTrackingService() {
        doOnTrackingService(WorkTimeTrackerService.ACTION_START)
    }

    private fun stopTrackingService() {
        doOnTrackingService(WorkTimeTrackerService.ACTION_STOP)
    }

    private fun doOnTrackingService(action: String) {
        val intent = Intent(getApplication(), WorkTimeTrackerService::class.java).apply {
            this.action = action
        }
        getApplication<Application>().startService(intent)
    }

    fun onComeEventDelete(comeEvent: ComeEvent?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEvent?.let { comeEventRepository.delete(it) }
            val message =
                getApplication<Application>().getString(R.string.work_day_details_come_events_deleted_message)
            viewModelScope.launch { mSnackbarMessage.value = message }
        }
    }

    fun onComeEventModified(modifiedComeEvent: ComeEvent) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEventRepository.save(modifiedComeEvent)
            val message =
                getApplication<Application>().getString(R.string.work_day_details_come_events_edited_message)
            viewModelScope.launch { mSnackbarMessage.value = message }
        }
    }

    fun onSnackbarShown() {
        mSnackbarMessage.value = null
    }

    override fun onRegisterNewEvent() {
        viewModelScope.launch {
            waitingFor.value = true

            val messageKey = when (comeEventUtils.registerNewEvent()) {
                ComeEventType.COME_IN -> R.string.dashboard_snackbar_info_income_registered
                ComeEventType.COME_OUT -> R.string.dashboard_snackbar_info_outcome_registered
            }

            val message = getApplication<Application>().getString(messageKey)
            mSnackbarMessage.value = message

            waitingFor.value = false
        }
    }
}

