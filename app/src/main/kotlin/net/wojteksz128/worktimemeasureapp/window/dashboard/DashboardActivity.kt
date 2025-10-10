package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityDashboardBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewSwipeCallback
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
import net.wojteksz128.worktimemeasureapp.window.dialog.dayOff.TodayDayOffInformationDialogFragment
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerLeftSwipeActionParams
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerRightSwipeActionParams
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    DeleteComeEventDialogListener, EditComeEventDialogListener {
    private val viewModel: DashboardViewModel by viewModels()
    private val selectedComeEventViewModel: SelectedComeEventViewModel by viewModels()

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var dayOffService: DayOffService

    @Inject
    lateinit var notificationService: WorkTimeNotificationService


    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    @Inject
    lateinit var timerManager: TimerManager

    private lateinit var comeEventsAdapter: ComeEventsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils, this, viewModel.ticker)

        val localViewModel = viewModel

        binding.apply {
            lifecycleOwner = this@DashboardActivity
            dateTimeUtils = this@DashboardActivity.dateTimeUtils
            viewModel = localViewModel
            dashboardCurrentDayEventsList.apply {
                adapter = comeEventsAdapter
                layoutManager = object : LinearLayoutManager(this@DashboardActivity) {
                    override fun canScrollVertically() = false
                }
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            this@DashboardActivity.baseContext?.let {
                val rvTouchCallback = RecyclerViewSwipeCallback(
                    ComeEventRecyclerLeftSwipeActionParams(it),
                    ComeEventRecyclerRightSwipeActionParams(it),
                    this@DashboardActivity::onEventSwiped
                )
                ItemTouchHelper(rvTouchCallback).attachToRecyclerView(dashboardCurrentDayEventsList)
            }
        }

        viewModel.workDay.observe(this@DashboardActivity) { workDay ->
            workDay?.let {
                comeEventsAdapter.submitList(it.events)
            }
        }

        viewModel.snackbarMessage.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onSnackbarShown()
            }
        }
    }

    private fun onEventSwiped(
        viewHolder: ComeEventViewHolder,
        direction: RecyclerViewSwipeCallback.Direction,
    ) {
        comeEventsAdapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
        viewHolder.binding.comeEvent?.let { comeEvent ->
            selectedComeEventViewModel.select(comeEvent)
        }
        when (direction) {
            RecyclerViewSwipeCallback.Direction.LEFT ->
                showDialog(EditComeEventDialogFragment::class.java)

            RecyclerViewSwipeCallback.Direction.RIGHT ->
                showDialog(DeleteComeEventDialogFragment::class.java)

            else -> {}
        }
    }

    private fun <DF : DialogFragment> showDialog(dialogFragmentClass: Class<DF>) {
        val dialog = dialogFragmentClass.getDeclaredConstructor().newInstance()
        dialog.show(supportFragmentManager, dialogFragmentClass.toString())
    }

    override fun onResume() {
        super.onResume()
        // TODO: 21.09.2021 Przenieś do innego miesca (niezależnego od DashboardActivity)
        dateTimeProvider.updateOffset()

        lifecycleScope.launch {
            val dayType = dayOffService.getDayType(dateTimeProvider.currentTime)
            if (dayType.isDayOff) {
                TodayDayOffInformationDialogFragment(dayType).show(
                    supportFragmentManager,
                    TODAY_DAY_OFF_DIALOG_TAG
                )
            }
        }
    }

    override fun onAcceptDeletionComeEventClick(dialog: DialogFragment) {
        viewModel.onComeEventDelete(selectedComeEventViewModel.selected.value)
    }

    override fun onAcceptModificationComeEventClick(
        dialog: DialogFragment,
        modifiedComeEvent: ComeEvent
    ) {
        viewModel.onComeEventModified(modifiedComeEvent)
    }

    companion object {
        const val TODAY_DAY_OFF_DIALOG_TAG = "TodayDayOffInformationDialog"
    }
}
