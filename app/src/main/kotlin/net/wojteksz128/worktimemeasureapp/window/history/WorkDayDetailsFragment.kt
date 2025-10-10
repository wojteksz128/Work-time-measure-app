package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDayDetailsBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewSwipeCallback
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerLeftSwipeActionParams
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerRightSwipeActionParams
import javax.inject.Inject

@AndroidEntryPoint
class WorkDayDetailsFragment : Fragment(), DeleteComeEventDialogListener,
    EditComeEventDialogListener {
    private val viewModel: WorkDayDetailsViewModel by viewModels()
    private val selectedComeEventViewModel: SelectedComeEventViewModel by activityViewModels()
    private val selectedWorkDayViewModel: SelectedWorkDayViewModel by activityViewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var settings: Settings

    private lateinit var binding: FragmentWorkDayDetailsBinding
    private lateinit var comeEventsAdapter: ComeEventsAdapter
    private lateinit var historyAdapter: WorkDayHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyAdapter = WorkDayHistoryAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils, viewLifecycleOwner, viewModel.ticker)
        binding = FragmentWorkDayDetailsBinding.inflate(layoutInflater, container, false)
        initializeLayoutData()
        viewModel.apply {
            fillWorkDayUsingLocal(selectedWorkDayViewModel.selected)
            workDay.observe(viewLifecycleOwner) {
                comeEventsAdapter.submitList(it.events)
            }
            history.observe(viewLifecycleOwner) { historyList ->
                historyAdapter.submitList(historyList)
            }
        }
        selectedWorkDayViewModel.selected.observe(viewLifecycleOwner) { workDay ->
            workDay.id?.let { workDayId ->
                viewModel.replaceWorkDayUsingRepository(
                    workDayId,
                    selectedWorkDayViewModel.selected
                )
            }
        }
        lifecycleScope.launch {
            viewModel.ticker.collectLatest {
                if (viewModel.workDay.value?.isWorkFinished() == false)
                    binding.invalidateAll()
            }
        }

        return binding.root
    }

    private fun initializeLayoutData() {
        binding.apply {
            lifecycleOwner = this@WorkDayDetailsFragment
            dateTimeUtils = this@WorkDayDetailsFragment.dateTimeUtils
            settings = this@WorkDayDetailsFragment.settings
            viewModel = this@WorkDayDetailsFragment.viewModel
            workDayDetailsComeEvents.apply {
                adapter = comeEventsAdapter
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically() = false
                }
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            workDayDetailsHistoryEntries.apply {
                adapter = historyAdapter
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically() = false
                }
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            context?.let {
                val rvTouchCallback = RecyclerViewSwipeCallback(
                    ComeEventRecyclerLeftSwipeActionParams(it),
                    ComeEventRecyclerRightSwipeActionParams(it),
                    this@WorkDayDetailsFragment::onEventSwiped
                )
                ItemTouchHelper(rvTouchCallback).attachToRecyclerView(workDayDetailsComeEvents)
            }
        }
    }

    private fun onEventSwiped(
        viewHolder: ComeEventViewHolder,
        direction: RecyclerViewSwipeCallback.Direction,
    ) {
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
        dialog.show(parentFragmentManager, dialogFragmentClass.toString())
    }

    override fun onAcceptDeletionComeEventClick(dialog: DialogFragment) {
        viewModel.onComeEventDelete(selectedComeEventViewModel.selected.value)
        Snackbar.make(
            binding.root,
            R.string.work_day_details_come_events_deleted_message,
            Snackbar.LENGTH_LONG
        ).show()
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
        val position = comeEventsAdapter.currentList.indexOfFirst { it.id == modifiedComeEvent.id }
        comeEventsAdapter.modifyCurrentList {
            if (position >= 0 && position < this.size) {
                selectedComeEventViewModel.changed =
                    this[position] != modifiedComeEvent || this[position].endDate != modifiedComeEvent.endDate
                this[position] = modifiedComeEvent
            }
        }

        Snackbar.make(
            binding.root,
            R.string.work_day_details_come_events_edited_message,
            Snackbar.LENGTH_LONG
        ).show()
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
}
