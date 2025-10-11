package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService.Companion.EXTRA_WORK_DAY
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeTrackerService.Companion.EXTRA_WORK_TIME_BALANCE
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.comeevent.NewEventRegisterListener
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalanceCalculator
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    workDayRepository: WorkDayRepository,
    private val comeEventRepository: ComeEventRepository,
    dateTimeProvider: DateTimeProvider,
    private val workTimeBalanceCalculator: WorkTimeBalanceCalculator,
    tickerFactory: TickerFactory,
    private val notificationService: WorkTimeNotificationService,
    private val comeEventUtils: ComeEventUtils,
) : AndroidViewModel(application), NewEventRegisterListener, ClassTagAware {
    val workDay: LiveData<WorkDay> =
        workDayRepository.getWorkDayByDateInLiveData(dateTimeProvider.currentDate)

    val workTimeBalance: LiveData<WorkTimeBalance> = workDay.switchMap { workDay ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val initialBalance = workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay)

            emit(initialBalance)

            if (!workDay.isWorkFinished())
                ticker.collect {
                    emit(workTimeBalanceCalculator.updateTodayBalance(workDay, initialBalance))
                }
        }
    }

    private val mSnackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = mSnackbarMessage

    val ticker: SharedFlow<Unit> = tickerFactory.create(viewModelScope)

    val waitingFor = MutableLiveData(false)

    init {
        workDay.observeForever { workDay ->
            handleNotifications(workDay, workTimeBalance.value)
        }
    }

    private fun handleNotifications(workDay: WorkDay?, workTimeBalance: WorkTimeBalance?) {
        if (workDay == null || workTimeBalance == null) return

        if (workDay.isWorkFinished()) {
            stopTrackingService()
            notificationService.cancelWorkInProgressNotification()
            notificationService.cancelEndOfWorkNotification()
        } else {
            startTrackingService(workDay, workTimeBalance)
            notificationService.showWorkInProgressNotification(workDay, workTimeBalance)
            notificationService.scheduleEndOfWorkNotification(workDay, workTimeBalance)
        }
    }

    private fun startTrackingService(
        workDay: WorkDay,
        workTimeBalance: WorkTimeBalance,
    ) {
        doOnTrackingService(WorkTimeTrackerService.ACTION_START) {
            putExtra(EXTRA_WORK_DAY, workDay)
            putExtra(EXTRA_WORK_TIME_BALANCE, workTimeBalance)
        }
    }

    private fun updateTrackingService(
        workDay: WorkDay,
        workTimeBalance: WorkTimeBalance,
    ) {
        doOnTrackingService(WorkTimeTrackerService.ACTION_UPDATE) {
            putExtra(EXTRA_WORK_DAY, workDay)
            putExtra(EXTRA_WORK_TIME_BALANCE, workTimeBalance)
        }
    }

    private fun stopTrackingService() {
        doOnTrackingService(WorkTimeTrackerService.ACTION_STOP)
    }

    private fun doOnTrackingService(action: String, block: (Intent.() -> Unit)? = null) {
        val intent = Intent(getApplication(), WorkTimeTrackerService::class.java).apply {
            this.action = action
            block?.let { this.it() }
        }
        getApplication<WorkTimeMeasureApp>().startService(intent)
    }

    fun onComeEventDelete(comeEvent: ComeEvent?) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEvent?.let { comeEventRepository.delete(it) }
            val message =
                getApplication<WorkTimeMeasureApp>().getString(R.string.work_day_details_come_events_deleted_message)
            viewModelScope.launch { mSnackbarMessage.value = message }
        }
    }

    fun onComeEventModified(modifiedComeEvent: ComeEvent) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            comeEventRepository.save(modifiedComeEvent)
            val message =
                getApplication<WorkTimeMeasureApp>().getString(R.string.work_day_details_come_events_edited_message)
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

            val message = getApplication<WorkTimeMeasureApp>().getString(messageKey)
            mSnackbarMessage.value = message

            waitingFor.value = false
        }
    }
}

