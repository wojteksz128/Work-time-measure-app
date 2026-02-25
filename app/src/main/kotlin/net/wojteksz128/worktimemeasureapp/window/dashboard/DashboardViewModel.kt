package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.comeevent.NewEventRegisterListener
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    workStateFlow: StateFlow<WorkState?>,
    private val comeEventRepository: ComeEventRepository,
    tickerFactory: TickerFactory,
    private val notificationService: WorkTimeNotificationService,
    private val comeEventUtils: ComeEventUtils,
) : AndroidViewModel(application), NewEventRegisterListener, ClassTagAware {
    val workState: LiveData<WorkState?> = workStateFlow.asLiveData()
    val workDay: LiveData<WorkDay?> = workState.map { it?.workDay }
    val workTimeBalance: LiveData<WorkTimeBalance?> = workState.map { it?.workTimeBalance }

    private val mSnackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = mSnackbarMessage

    val ticker: SharedFlow<Unit> = tickerFactory.create(viewModelScope)

    val waitingFor = MutableLiveData(false)

    private var wasWorkFinished: Boolean? = null

    init {
        workState.observeForever { workState ->
            handleServiceAndNotifications(workState)
        }
    }

    private fun handleServiceAndNotifications(workState: WorkState?) {
        val isWorkFinished = workState?.workDay?.isWorkFinished() ?: true

        if (isWorkFinished == wasWorkFinished) return

        if (isWorkFinished) {
            stopTrackingService()
            notificationService.cancelEndOfWorkNotification()
        } else {
            startTrackingService()
            workState?.let { workState ->
                notificationService.scheduleEndOfWorkNotification(
                    workState.workTimeBalance
                )
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

