package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDaysHistoryBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewSwipeCallback
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
import net.wojteksz128.worktimemeasureapp.window.dialog.showDialogWithListener
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.history.WorkDayAdapter.WorkDayItemListener
import javax.inject.Inject

@AndroidEntryPoint
class WorkDaysHistoryFragment : Fragment(), ClassTagAware, WorkDayItemListener,
    DeleteComeEventDialogListener, EditComeEventDialogListener {
    private val viewModel: WorkDaysHistoryViewModel by viewModels()
    private val selectedWorkDayViewModel: SelectedWorkDayViewModel by activityViewModels()
    private val selectedComeEventViewModel: SelectedComeEventViewModel by activityViewModels()

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    private lateinit var binding: FragmentWorkDaysHistoryBinding
    private lateinit var workDayAdapter: WorkDayAdapter

    private var swipedComeEventsAdapter: ComeEventsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWorkDaysHistoryBinding.inflate(layoutInflater, container, false)

        binding.workDaysHistoryRv.apply {
            val workDayAdapter =
                WorkDayAdapter(
                    requireContext(),
                    viewLifecycleOwner,
                    viewModel.ticker,
                    this@WorkDaysHistoryFragment
                )
            adapter = workDayAdapter.also { this@WorkDaysHistoryFragment.workDayAdapter = it }
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.v(classTag, "onCreateView: Fill days list")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.workDaysPager.collectLatest { pagingData ->
                    workDayAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onWorkDayEventSelected(
        viewHolder: ComeEventViewHolder,
        direction: RecyclerViewSwipeCallback.Direction,
    ) {
        val childAdapter = viewHolder.bindingAdapter as? ComeEventsAdapter ?: return
        swipedComeEventsAdapter = childAdapter

        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val itemUiModel = childAdapter.currentList[position]
            selectedComeEventViewModel.select(itemUiModel.originalEntity)
        }
        when (direction) {
            RecyclerViewSwipeCallback.Direction.LEFT -> showDialogWithListener(
                EditComeEventDialogFragment::class.java, parentFragmentManager, this
            )

            RecyclerViewSwipeCallback.Direction.RIGHT -> showDialogWithListener(
                DeleteComeEventDialogFragment::class.java, parentFragmentManager, this
            )

            else -> {}
        }
    }

    override fun onWorkDayClicked(workDay: WorkDay) {
        selectedWorkDayViewModel.select(workDay)
        findNavController().navigate(R.id.viewWorkDayDetails, bundleOf())
    }

    override fun onAcceptDeletionComeEventClick(dialog: DialogFragment) {
        val selectedEvent = selectedComeEventViewModel.selected.value ?: return

        viewModel.onComeEventDelete(selectedEvent)

        Snackbar.make(
            binding.root,
            R.string.history_come_events_deleted_message,
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
        selectedComeEventViewModel.changed = true

        Snackbar.make(
            binding.root,
            R.string.history_come_events_edited_message,
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
            swipedComeEventsAdapter?.let { childAdapter ->
                val position = childAdapter.currentList.indexOfFirst { it.id == selectedEvent.id }
                if (position >= 0) {
                    childAdapter.notifyItemChanged(position)
                }
            }
        }

        swipedComeEventsAdapter = null
    }
}