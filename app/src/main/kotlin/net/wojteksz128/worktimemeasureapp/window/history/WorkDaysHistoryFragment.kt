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
import androidx.navigation.fragment.findNavController
import androidx.paging.liveData
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDaysHistoryBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.recyclerView.ViewHolderInformation
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
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
    lateinit var dateTimeUtils: DateTimeUtils

    private lateinit var binding: FragmentWorkDaysHistoryBinding
    private lateinit var workDayAdapter: WorkDayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkDaysHistoryBinding.inflate(layoutInflater, container, false)

        binding.workDaysHistoryRv.apply {
            val workDayAdapter =
                WorkDayAdapter(
                    requireContext(),
                    dateTimeUtils,
                    childFragmentManager,
                    viewLifecycleOwner,
                    this@WorkDaysHistoryFragment
                )
            adapter = workDayAdapter.also { this@WorkDaysHistoryFragment.workDayAdapter = it }
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        Log.v(classTag, "onCreateView: Fill days list")
        viewModel.workDaysPager.liveData.observe(viewLifecycleOwner) {
            workDayAdapter.submitData(this.lifecycle, it)
        }

        return binding.root
    }

    override fun onWorkDayEventSelected(
        comeEvent: ComeEvent,
        viewHolderInformation: ViewHolderInformation<ComeEventViewHolder>
    ) {
        selectedComeEventViewModel.select(comeEvent, viewHolderInformation)
    }

    override fun onWorkDayClicked(workDay: WorkDay): (View) -> Unit = {
        selectedWorkDayViewModel.select(workDay)
        findNavController().navigate(R.id.viewWorkDayDetails, bundleOf())
    }

    override fun onAcceptDeletionComeEventClick(dialog: DialogFragment) {
        viewModel.onComeEventDelete(selectedComeEventViewModel.selected.value)
        Snackbar.make(
            binding.root,
            R.string.history_come_events_deleted_message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onRejectDeletionComeEventClick(dialog: DialogFragment) {
        // Nothing to do
    }

    override fun onDeleteComeEventDialogDismiss(dialog: DialogFragment) {
        selectedComeEventViewModel.viewHolderInformation?.adapter?.notifyDataSetChanged()
    }

    override fun onAcceptModificationComeEventClick(
        dialog: DialogFragment,
        modifiedComeEvent: ComeEvent
    ) {
        viewModel.onComeEventModified(modifiedComeEvent)
        Snackbar.make(
            binding.root,
            R.string.history_come_events_edited_message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onRejectModificationComeEventClick(dialog: DialogFragment) {
        // Nothing to do
    }

    override fun onEditComeEventDialogDismiss(dialog: DialogFragment) {
        selectedComeEventViewModel.viewHolderInformation?.adapter?.notifyDataSetChanged()
    }
}