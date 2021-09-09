package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.databinding.ActivityDashboardBinding
import net.wojteksz128.worktimemeasureapp.job.WaitForEndOfWorkJob
import net.wojteksz128.worktimemeasureapp.notification.InWorkNotification
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.*
import net.wojteksz128.worktimemeasureapp.util.coroutines.WorkTimeTimer
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ItemUpdate
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter
import java.util.*

class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    ClassTagAware {
    private val viewModel: DashboardViewModel by viewModels()

    private val comeEventsAdapter = ComeEventsAdapter()
    private val currentDayObserver = CurrentDayObserver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@DashboardActivity
            viewModel = this@DashboardActivity.viewModel
            workTimeData = this@DashboardActivity.viewModel.workTimeData
            dashboardCurrentDayEventsList.adapter = comeEventsAdapter
            dashboardCurrentDayEventsList.layoutManager = object : LinearLayoutManager(this@DashboardActivity) {
                override fun canScrollVertically() = false
            }
        }

        initFab()
        // TODO: 02.09.2021 move to another place
        NotificationUtils.initNotifications(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d(classTag, "onResume: Fill days list")
        viewModel.workDay.observe(this@DashboardActivity, currentDayObserver)
        DateTimeProvider.updateOffset(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d(classTag, "onPause: Stop second updater")
        viewModel.workTimeCounterRunner?.let { WorkTimeTimer.cancelTimer(it) }
    }

    private fun initFab() {
        val enterFab: FloatingActionButton = binding.dashboardEnterFab
        enterFab.setOnClickListener {
            ComeEventUtils.registerNewEvent(this@DashboardActivity,
                    {
                        viewModel.waitingFor.value = true
                    },
                    { input ->
                        val message: String = when (input) {
                            ComeEventType.COME_IN -> {
                                // TODO: 12.07.2019 Store scheduleJob
                                if (Settings.WorkTime.NotifyingEnabled.valueNullable == true) {
                                    WaitForEndOfWorkJob.schedule(this)
                                    InWorkNotification(this).notifyUser()
                                }
                                getString(R.string.dashboard_snackbar_info_income_registered)
                            }
                            ComeEventType.COME_OUT -> getString(R.string.dashboard_snackbar_info_outcome_registered)
                        }

                        viewModel.waitingFor.value = false

                        Snackbar.make(baseContainer, message, Snackbar.LENGTH_LONG).show()
                    })
        }
    }


    private inner class CurrentDayObserver : Observer<WorkDayEvents> {

        override fun onChanged(workDayEvents: WorkDayEvents?) {
            viewModel.workTimeData.value?.updateData()

            workDayEvents?.let { dayEvents ->
                comeEventsAdapter.submitList(dayEvents.events)
                fillNotEndedEventsIndexList(dayEvents)
                runUpdaterEverySecond()
            }
        }

        private fun fillNotEndedEventsIndexList(dayEvents: WorkDayEvents) {
            dayEvents.events.forEachIndexed { index, comeEvent ->
                if (!comeEvent.isEnded)
                    viewModel.notEndedEventsIndex.add(ItemUpdate(index))
                else {
                    comeEventsAdapter.notifyItemChanged(index)
                    viewModel.notEndedEventsIndex.forEach { if (it.position == index) it.lastIteration = true }
                }
            }
        }

        private fun runUpdaterEverySecond() =
            if (viewModel.notEndedEventsIndex.isNotEmpty()) {
                val params = WorkTimeTimer.WorkTimeTimerParams(repeatMillis = 1000,
                    mainThreadAction = {
                        viewModel.workTimeData.value?.updateData()
                        viewModel.notEndedEventsIndex.forEach { index ->
                            comeEventsAdapter.notifyItemChanged(index.position)
                        }
                        viewModel.notEndedEventsIndex.removeAll { it.lastIteration }
                    })
                viewModel.workTimeCounterRunner = WorkTimeTimer.startTimer(params)
            } else {
                viewModel.workTimeCounterRunner?.let { WorkTimeTimer.cancelTimer(it) }
            }
    }
}
