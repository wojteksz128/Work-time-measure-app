package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityDashboardBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.*
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.comeevent.NewEventRegisterListener
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    NewEventRegisterListener, ClassTagAware {
    private val viewModel: DashboardViewModel by viewModels()

    @Inject
    lateinit var comeEventUtils: ComeEventUtils
    @Inject
    lateinit var dateTimeProvider: DateTimeProvider
    @Inject
    lateinit var dateTimeUtils: DateTimeUtils
    @Inject
    lateinit var notificationUtils: NotificationUtils
    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings
    @Inject
    lateinit var timerManager: TimerManager

    private lateinit var comeEventsAdapter: ComeEventsAdapter
    private val currentDayObserver = CurrentDayObserver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)

        binding.apply {
            lifecycleOwner = this@DashboardActivity
            dateTimeUtils = this@DashboardActivity.dateTimeUtils
            viewModel = this@DashboardActivity.viewModel
            workTimeData = this@DashboardActivity.viewModel.workTimeData
            newEventRegisterListener = this@DashboardActivity
            dashboardCurrentDayEventsList.apply {
                adapter = comeEventsAdapter
                layoutManager = object : LinearLayoutManager(this@DashboardActivity) {
                    override fun canScrollVertically() = false
                }
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(classTag, "onResume: Fill days list")
        viewModel.workDay.observe(this@DashboardActivity, currentDayObserver)
        viewModel.workDay.value?.let { runTimerIfRequiredFor(it) }
        // TODO: 21.09.2021 Przenieś do innego miesca (niezależnego od DashboardActivity)
        dateTimeProvider.updateOffset(this)
    }

    private fun runTimerIfRequiredFor(workDay: WorkDay) {
        if (workDay.events.any { !it.isEnded }) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    private fun startTimer() {
        if (viewModel.workTimeCounterRunner?.isActive != true) {
            val params = PeriodicOperation.PeriodicOperationParams(repeatMillis = 1000,
                mainThreadAction = {
                    Log.d(classTag, "startTimer: Update workTimeData")
                    viewModel.workTimeData.value?.updateData()
                })
            viewModel.workTimeCounterRunner = PeriodicOperation.start(params)
            comeEventsAdapter.syncUpdaterWith(viewModel.workTimeCounterRunner!!)
        }
    }

    private fun stopTimer() {
        viewModel.workTimeCounterRunner?.let { PeriodicOperation.cancel(it) }
    }

    override fun onPause() {
        Log.d(classTag, "onPause: Stop second updater")
        stopTimer()
        super.onPause()
    }

    override fun registerNewEvent() {
        lifecycleScope.launch {
            viewModel.waitingFor.value = true

            val message = when (comeEventUtils.registerNewEvent()) {
                ComeEventType.COME_IN -> {
                    if (Settings.WorkTime.NotifyingEnabled.valueNullable == true) {
                        notificationUtils.notifyUserAboutWorkTime(viewModel.workTimeData.value!!)
                    }
                    getString(R.string.dashboard_snackbar_info_income_registered)
                }
                ComeEventType.COME_OUT -> {
                    timerManager.removeAlarm()
                    getString(R.string.dashboard_snackbar_info_outcome_registered)
                }
            }

            viewModel.waitingFor.value = false

            Snackbar.make(baseContainer, message, Snackbar.LENGTH_LONG).show()
        }
    }


    private inner class CurrentDayObserver : Observer<WorkDay> {

        override fun onChanged(workDayEvents: WorkDay?) {
            viewModel.workTimeData.value?.updateData()

            workDayEvents?.let { dayEvents ->
                comeEventsAdapter.submitList(dayEvents.events)
                runTimerIfRequiredFor(dayEvents)
            }
        }

    }
}
