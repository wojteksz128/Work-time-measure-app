package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityDashboardBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.notification.worktime.WorkTimeNotificationService
import net.wojteksz128.worktimemeasureapp.service.DayOffService
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewSwipeCallback
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
import net.wojteksz128.worktimemeasureapp.window.dialog.dayOff.TodayDayOffInformationDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.showDialogWithListener
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerLeftSwipeActionParams
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerRightSwipeActionParams
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(R.layout.activity_dashboard),
    DeleteComeEventDialogListener, EditComeEventDialogListener {
    val viewModel: DashboardViewModel by viewModels()
    private val selectedComeEventViewModel: SelectedComeEventViewModel by viewModels()

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

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

        comeEventsAdapter =
            ComeEventsAdapter(this, viewModel.ticker)

        val localViewModel = viewModel

        binding.apply {
            lifecycleOwner = this@DashboardActivity
            viewModel = localViewModel
            dashboardCurrentDayEventsList.apply {
                adapter = comeEventsAdapter
                layoutManager = object : LinearLayoutManager(this@DashboardActivity) {
                    override fun canScrollVertically() = false
                }
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect

                    binding.apply {
                        dashboardRemainingDayTime.binding.durationText =
                            state.standardRemainingTodayText
                        dashboardMonthlyBalance.binding.durationText = state.monthlyBalanceText
                        dashboardTodayWorkTime.binding.durationText = state.todayWorkTimeText
                        dashboardCurrentDayDate.text = state.currentDayLabel
                        dashboardCurrentDayEventsList.visibility =
                            if (state.isEventsListVisible) View.VISIBLE else View.INVISIBLE
                        dashboardCurrentDayEmptyEventsMessage.visibility =
                            if (state.isNoEventsLabelVisible) View.VISIBLE else View.INVISIBLE
                        dashboardLoadingIndicator.visibility =
                            if (state.isLoading) View.VISIBLE else View.INVISIBLE

                        dashboardEnterFab.setOnClickListener { viewModel!!.onRegisterNewEvent() }
                    }

                    comeEventsAdapter.submitList(state.comeEvents)
                }
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
        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val itemUiModel = comeEventsAdapter.currentList[position]
            selectedComeEventViewModel.select(itemUiModel.originalEntity)
        }

        when (direction) {
            RecyclerViewSwipeCallback.Direction.LEFT -> showDialogWithListener(
                EditComeEventDialogFragment::class.java, supportFragmentManager, this
            )

            RecyclerViewSwipeCallback.Direction.RIGHT -> showDialogWithListener(
                DeleteComeEventDialogFragment::class.java, supportFragmentManager, this
            )

            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        // TODO: 21.09.2021 Przenieś do innego miejsca (niezależnego od DashboardActivity)
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

    override fun onDeleteComeEventDialogDismiss(dialog: DialogFragment) {
        super.onDeleteComeEventDialogDismiss(dialog)
        resetSwipedItemView()
    }

    override fun onAcceptModificationComeEventClick(
        dialog: DialogFragment,
        modifiedComeEvent: ComeEvent
    ) {
        viewModel.onComeEventModified(modifiedComeEvent)
        selectedComeEventViewModel.changed = true
    }

    override fun onEditComeEventDialogDismiss(dialog: DialogFragment) {
        super.onEditComeEventDialogDismiss(dialog)
        if (!selectedComeEventViewModel.changed)
            resetSwipedItemView()
        selectedComeEventViewModel.changed = false
    }

    private fun resetSwipedItemView() {
        selectedComeEventViewModel.selected.value?.let { selectedEvent ->
            val position = comeEventsAdapter.currentList.indexOfFirst { it.id == selectedEvent.id }
            if (position >= 0)
                comeEventsAdapter.notifyItemChanged(position)
        }
    }

    companion object {
        const val TODAY_DAY_OFF_DIALOG_TAG = "TodayDayOffInformationDialog"
    }
}
