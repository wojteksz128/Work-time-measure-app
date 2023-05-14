package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityDashboardBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.ComeEventType
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.comeevent.NewEventRegisterListener
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
import net.wojteksz128.worktimemeasureapp.window.dialog.dayOff.TodayDayOffInformationDialogFragment
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventsRecyclerViewSwipeLogic
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    NewEventRegisterListener, DeleteComeEventDialogListener, EditComeEventDialogListener,
    ClassTagAware {
    private val viewModel: DashboardViewModel by viewModels()
    private val selectedComeEventViewModel: SelectedComeEventViewModel by viewModels()

    @Inject
    lateinit var comeEventUtils: ComeEventUtils

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var dayOffService: DayOffService

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
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            this@DashboardActivity.baseContext?.let {
                ComeEventsRecyclerViewSwipeLogic(
                    it
                ) { comeEvent, _ ->
                    selectedComeEventViewModel.select(comeEvent)
                }.attach(dashboardCurrentDayEventsList, supportFragmentManager)
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

        lifecycleScope.launch {
            val dayType = dayOffService.getDayType(dateTimeProvider.currentTime)
            if (dayType.isDayOff && !viewModel.dayOffDialogShowed) {
                viewModel.dayOffDialogShowed = true
                TodayDayOffInformationDialogFragment(dayType).show(
                    supportFragmentManager,
                    TODAY_DAY_OFF_DIALOG_TAG
                )
            }
        }
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

        override fun onChanged(value: WorkDay) {
            viewModel.workTimeData.value?.updateData()

            comeEventsAdapter.submitList(value.events)
            runTimerIfRequiredFor(value)
        }

    }

    override fun onAcceptDeletionComeEventClick(dialog: DialogFragment) {
        viewModel.onComeEventDelete(selectedComeEventViewModel.selected.value)
        Snackbar.make(
            binding.root,
            R.string.work_day_details_come_events_deleted_message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onRejectDeletionComeEventClick(dialog: DialogFragment) {
        // Nothing to do
    }

    override fun onDeleteComeEventDialogDismiss(dialog: DialogFragment) {
        comeEventsAdapter.notifyDataSetChanged()
    }

    override fun onAcceptModificationComeEventClick(
        dialog: DialogFragment,
        modifiedComeEvent: ComeEvent
    ) {
        viewModel.onComeEventModified(modifiedComeEvent)
        Snackbar.make(
            binding.root,
            R.string.work_day_details_come_events_edited_message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onRejectModificationComeEventClick(dialog: DialogFragment) {
        // Nothing to do
    }

    override fun onEditComeEventDialogDismiss(dialog: DialogFragment) {
        comeEventsAdapter.notifyDataSetChanged()
    }

    companion object {
        const val TODAY_DAY_OFF_DIALOG_TAG = "TodayDayOffInformationDialog"
    }
}
