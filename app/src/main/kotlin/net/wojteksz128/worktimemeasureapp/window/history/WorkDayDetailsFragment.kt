package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDayDetailsBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogFragment.DeleteComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogFragment.EditComeEventDialogListener
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.SelectedComeEventViewModel
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventsRecyclerViewSwipeLogic
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkDayDetailsBinding.inflate(layoutInflater, container, false)
        initializeLayoutData()
        viewModel.apply {
            fillWorkDayUsingLocal(selectedWorkDayViewModel.selected)
            workDay.observe(viewLifecycleOwner) {
                comeEventsAdapter.submitList(it.events)
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
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            context?.let {
                ComeEventsRecyclerViewSwipeLogic(
                    it,
                    selectedComeEventViewModel.selected,
                    this@WorkDayDetailsFragment.viewModel.modifiedComeEventPosition
                ).attach(workDayDetailsComeEvents, childFragmentManager)
            }
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

    override fun onModifyComeEventClick(dialog: DialogFragment, modifiedComeEvent: ComeEvent) {
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
}
