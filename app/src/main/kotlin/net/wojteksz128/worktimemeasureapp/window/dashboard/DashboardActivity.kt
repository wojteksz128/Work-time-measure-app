package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Observer
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
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils.formatCounterTime
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils.mergeComeEventsDuration
import net.wojteksz128.worktimemeasureapp.util.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventViewHolder
import java.util.*

class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    ClassTagAware {
    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var currentDayObserver: CurrentDayObserver
    private lateinit var lastWeekObserver: LastWeekObserver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@DashboardActivity
            viewModel = this@DashboardActivity.viewModel
            workTimeData = this@DashboardActivity.viewModel.workTimeData.value
        }

        initFab()
        // TODO: 02.09.2021 move to another place
        NotificationUtils.initNotifications(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d(classTag, "onResume: Fill days list")
        currentDayObserver = CurrentDayObserver()
        lastWeekObserver = LastWeekObserver()
        viewModel.workTimeData.value?.workDay?.observe(this, currentDayObserver)
        viewModel.workTimeData.value?.weekWorkDays?.observe(this, lastWeekObserver)
        DateTimeProvider.updateOffset(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d(classTag, "onPause: Stop second updater")
        this.viewModel.secondRunner.stop()
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

    private inner class CurrentDayObserver : Observer<WorkDayEvents>, ClassTagAware {

        override fun onChanged(workDayEvents: WorkDayEvents?) {
            workDayEvents?.let {
                fillRemainingWorkDayTime(it)
                fillTodayWorkTime(it)
                fillTodayEvents(it)

                if (!it.hasEventsEnded()) {
                    if (!viewModel.secondRunner.isRunning) {
                        viewModel.secondRunner.setConsumer(getUpdateAction(it))
                        Log.v(classTag, "onChanged: start second updater")
                        viewModel.secondRunner.start()
                    }
                }
            }
        }

        private fun fillRemainingWorkDayTime(it: WorkDayEvents) {
            // TODO: 29.08.2021 Zmień sposób realizacji liczenia czasu pracy
            val workDayDuration = Settings.WorkTime.Duration.value.millis
            val elapsedTime = workDayDuration - mergeComeEventsDuration(it).time
            val formatDate = formatCounterTime(elapsedTime)
            viewModel.workTimeData.value?.remainingTodayWorkTime = formatDate

        }

        private fun fillTodayWorkTime(it: WorkDayEvents) {
            val formatDate = DateTimeUtils.formatDate(
                getString(R.string.history_work_day_duration_format),
                mergeComeEventsDuration(it),
                TimeZone.getTimeZone("UTC")
            )
            viewModel.workTimeData.value?.todayWorkTime = formatDate
        }

        private fun fillTodayEvents(it: WorkDayEvents) {
            val formatDate = DateTimeUtils.formatDate(
                getString(R.string.history_work_day_label_format),
                it.workDay.date
            )
            viewModel.workTimeData.value?.currentDayDate = formatDate

            if (it.events.isNotEmpty()) {
                val inflater = LayoutInflater.from(this@DashboardActivity)
                binding.dashboardCurrentDayEventsList.removeAllViews()

                it.events.forEach {
                    val eventViewHolder = ComeEventViewHolder(
                        inflater.inflate(
                            R.layout.history_day_event_list_item,
                            binding.dashboardCurrentDayEventsList,
                            false
                        )
                    )
                    eventViewHolder.bind(it)
                    binding.dashboardCurrentDayEventsList.addView(eventViewHolder.view)
                }
            }
        }

        private fun getUpdateAction(currentDay: WorkDayEvents): FunctionWithParameter<WorkDayEvents> {
            return object : FunctionWithParameter<WorkDayEvents>(currentDay) {

                override fun action(obj: WorkDayEvents) {
                    Log.v(classTag, "onChanged: Update work day")
                    runOnUiThread {
                        lastWeekObserver.onChanged(viewModel.workTimeData.value?.weekWorkDays?.value)
                        currentDayObserver.onChanged(viewModel.workTimeData.value?.workDay?.value)
                    }
                }

            }
        }

    }

    private inner class LastWeekObserver : Observer<List<WorkDayEvents>> {

        override fun onChanged(updatedCollection: List<WorkDayEvents>?) {
            updatedCollection?.let {
                fillRemainingWeekWorkDayTime(it)
            }
        }

        private fun fillRemainingWeekWorkDayTime(updatedCollection: List<WorkDayEvents>) {
            val weekWorkDaysTime =
                updatedCollection.sumOf { mergeComeEventsDuration(it).time }
            // TODO: 29.08.2021 Zmień sposób realizacji liczenia czasu pracy
            // TODO: 30.08.2021 Obsługa określenia dni roboczych
            // TODO: 30.08.2021 Obsługa określenia nierównomiernych dni roboczych
            val weekWorkDuration = Settings.WorkTime.Duration.value.millis.times(5)
            val elapsedTime = weekWorkDuration - weekWorkDaysTime
            viewModel.workTimeData.value?.remainingWeekWorkTime = formatCounterTime(elapsedTime)
        }

    }
}
