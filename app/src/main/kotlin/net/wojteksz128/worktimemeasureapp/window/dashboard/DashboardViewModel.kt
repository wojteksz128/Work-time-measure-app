package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
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
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.settings.Settings
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
    private val settings: Settings,
) : AndroidViewModel(application), NewEventRegisterListener, ClassTagAware {
    val workDay: LiveData<WorkDay> =
        workDayRepository.getWorkDayByDateInLiveData(dateTimeProvider.currentDate)

    val workTimeBalance: LiveData<WorkTimeBalance> = workDay.switchMap { workDay ->
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay))
            ticker.collect {
                emit(workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay))
            }
        }
    }

    private val mSnackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = mSnackbarMessage

    val ticker: SharedFlow<Unit> = tickerFactory.create(viewModelScope)

    val waitingFor = MutableLiveData(false)

    init {
        workDay.observeForever { workDay ->
            val workTimeBalance = this@DashboardViewModel.workTimeBalance.value
            if (workDay != null && workTimeBalance != null)
                if (workDay.isWorkFinished())
                    notificationService.cancelWorkInProgressNotification()
                else
                    notificationService.showWorkInProgressNotification(workDay, workTimeBalance)
        }

        workTimeBalance.observeForever { workTimeBalance ->
            val workDay = workDay.value
            if (workDay != null)
                if (workDay.isWorkFinished()) {
                    val startTime = workDay.events.lastOrNull()?.startDate
                    if (startTime != null) {
                        val balancedEndTime = startTime.plus(workTimeBalance.remainingTodayWorkTime)
                            .minus(workTimeBalance.monthlyBalance)
                        notificationService.scheduleEndOfWorkNotification(balancedEndTime)
                    }
                } else {
                    notificationService.cancelEndOfWorkNotification()
                }
        }
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
                ComeEventType.COME_IN -> {
                    if (settings.WorkTime.NotifyingEnabled.valueNullable == true)
                        workDay.value?.let { workDay ->
                            workTimeBalance.value?.let { balance ->
                                notificationService.showWorkInProgressNotification(workDay, balance)
                            }
                        }

                    R.string.dashboard_snackbar_info_income_registered
                }

                ComeEventType.COME_OUT -> {
                    notificationService.cancelWorkInProgressNotification()
                    notificationService.cancelEndOfWorkNotification()
                    R.string.dashboard_snackbar_info_outcome_registered
                }
            }

            val message = getApplication<WorkTimeMeasureApp>().getString(messageKey)
            mSnackbarMessage.value = message

            waitingFor.value = false
        }
    }
}

