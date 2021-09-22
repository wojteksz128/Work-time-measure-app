package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.databinding.ActivityDashboardBinding
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.*
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.comeevent.NewEventRegisterListener
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter
import java.util.*

class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    NewEventRegisterListener, ClassTagAware {
    private val viewModel: DashboardViewModel by viewModels()

    private val comeEventsAdapter = ComeEventsAdapter()
    private val currentDayObserver = CurrentDayObserver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@DashboardActivity
            viewModel = this@DashboardActivity.viewModel
            workTimeData = this@DashboardActivity.viewModel.workTimeData
            newEventRegisterListener = this@DashboardActivity
            dashboardCurrentDayEventsList.adapter = comeEventsAdapter
            dashboardCurrentDayEventsList.layoutManager = object : LinearLayoutManager(this@DashboardActivity) {
                override fun canScrollVertically() = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(classTag, "onResume: Fill days list")
        viewModel.workDay.observe(this@DashboardActivity, currentDayObserver)
        viewModel.workDay.value?.let { runTimerIfRequiredFor(it) }
        // TODO: 21.09.2021 Przenieś do innego miesca (niezależnego od DashboardActivity)
        DateTimeProvider.updateOffset(this)
    }

    private fun runTimerIfRequiredFor(workDayEvents: WorkDayEvents) {
        if (workDayEvents.events.any { !it.isEnded }) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    private fun startTimer() {
        if (viewModel.workTimeCounterRunner?.timer?.isActive != true) {
            val params = PeriodicOperation.PeriodicOperationParams(repeatMillis = 1000,
                mainThreadAction = {
                    Log.d(classTag, "startTimer: Update workTimeData")
                    viewModel.workTimeData.value?.updateData()
                })
            viewModel.workTimeCounterRunner = PeriodicOperation.start(params)
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

            val comeEventType: ComeEventType

            withContext(Dispatchers.IO) {
                comeEventType = ComeEventUtils.registerNewEvent(this@DashboardActivity)
            }

            val message = when (comeEventType) {
                ComeEventType.COME_IN -> {
                    if (Settings.WorkTime.NotifyingEnabled.valueNullable == true) {
                        NotificationUtils.notifyUserAboutWorkTime(this@DashboardActivity, viewModel.workTimeData.value!!)
                    }
                    getString(R.string.dashboard_snackbar_info_income_registered)
                }
                ComeEventType.COME_OUT -> {
                    TimerManager.removeAlarm(this@DashboardActivity)
                    getString(R.string.dashboard_snackbar_info_outcome_registered)
                }
            }

            viewModel.waitingFor.value = false

            Snackbar.make(baseContainer, message, Snackbar.LENGTH_LONG).show()
        }
    }


    private inner class CurrentDayObserver : Observer<WorkDayEvents> {

        override fun onChanged(workDayEvents: WorkDayEvents?) {
            viewModel.workTimeData.value?.updateData()

            workDayEvents?.let { dayEvents ->
                comeEventsAdapter.submitList(dayEvents.events)
                runTimerIfRequiredFor(dayEvents)
            }
        }

    }
}
