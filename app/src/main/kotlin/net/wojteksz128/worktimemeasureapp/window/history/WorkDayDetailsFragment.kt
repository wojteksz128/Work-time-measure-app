package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.DialogComeEventEditBinding
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDayDetailsBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.dialog.DialogComeEventEditViewModel
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerSwipeHelper
import javax.inject.Inject

@AndroidEntryPoint
class WorkDayDetailsFragment : Fragment() {
    private val viewModel: WorkDayDetailsViewModel by viewModels()
    private val selectedWorkDayViewModel: SelectedWorkDayViewModel by activityViewModels()
    private val editDialogViewModel: DialogComeEventEditViewModel by viewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var settings: Settings

    private lateinit var binding: FragmentWorkDayDetailsBinding
    private lateinit var comeEventsAdapter: ComeEventsAdapter
    private lateinit var deleteComeEventDialog: AlertDialog
    private lateinit var editComeEventDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)

        deleteComeEventDialog = prepareDeleteComeEventDialog()
        editComeEventDialog = prepareEditComeEventDialog()
    }

    private fun prepareDeleteComeEventDialog() = AlertDialog.Builder(requireContext()).apply {
        setTitle(R.string.work_day_details_come_events_action_delete_title)
        setPositiveButton(R.string.work_day_details_come_events_action_delete) { _, _ ->
            viewModel.onComeEventDelete()
            viewModel.comeEventPosition.value?.let { comeEventsAdapter.notifyItemRemoved(it) }
            Snackbar.make(
                binding.root,
                R.string.work_day_details_come_events_deleted_message,
                Snackbar.LENGTH_LONG
            ).show()
        }
        setNegativeButton(R.string.work_day_details_come_events_action_cancel) { _, _ ->
            viewModel.comeEventPosition.value?.let { comeEventsAdapter.notifyItemRemoved(it) }
        }
    }.create()

    private fun prepareEditComeEventDialog() = AlertDialog.Builder(requireContext()).apply {
        val dialogBinding = DialogComeEventEditBinding.inflate(layoutInflater, null, false)
            .apply {
                this.lifecycleOwner = this@WorkDayDetailsFragment
                this.dateTimeUtils = this@WorkDayDetailsFragment.dateTimeUtils
                this.viewModel = this@WorkDayDetailsFragment.editDialogViewModel
            }
        setView(dialogBinding.root)
        setTitle(R.string.work_day_details_come_events_action_edit_title)
        setPositiveButton(R.string.work_day_datails_come_events_action_edit) { _, _ ->
            viewModel.comeEventPosition.value?.let { comeEventsAdapter.notifyItemRemoved(it) }
            Snackbar.make(
                binding.root,
                R.string.work_day_details_come_events_edited_message,
                Snackbar.LENGTH_LONG
            ).show()
        }
        setNegativeButton(R.string.work_day_details_come_events_action_cancel) { _, _ ->
            viewModel.comeEventPosition.value?.let { comeEventsAdapter.notifyItemRemoved(it) }
        }
    }.create()

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
            comeEventToDelete.observe(viewLifecycleOwner) {
                val message = viewModel.prepareDeleteMessage(it)
                deleteComeEventDialog.setMessage(message)
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
            initializeSwipeLogic(workDayDetailsComeEvents)
        }
    }

    private fun initializeSwipeLogic(workDayDetailsComeEvents: RecyclerView) {
        val swipeLeft = RecyclerLeftSwipeActionParam(
            R.color.teal_200,
            R.drawable.ic_baseline_edit_24,
            requireContext(),
            this::processEditComeEvent
        )
        val swipeRight = RecyclerRightSwipeActionParam(
            R.color.colorAlert,
            R.drawable.ic_baseline_delete_24,
            requireContext(),
            this::processDeleteComeEvent
        )
        val recyclerSwipeHelper = RecyclerSwipeHelper(
            swipeLeft,
            swipeRight
        ) { (it as ComeEventsAdapter.ComeEventViewHolder).binding.comeEvent!! }
        val itemTouchHelper = ItemTouchHelper(recyclerSwipeHelper)
        itemTouchHelper.attachToRecyclerView(workDayDetailsComeEvents)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun processEditComeEvent(comeEvent: ComeEvent, position: Int) {
        editDialogViewModel.fill(comeEvent)
        viewModel.comeEventPosition.value = position
        editComeEventDialog.show()
    }

    private fun processDeleteComeEvent(comeEvent: ComeEvent, position: Int) {
        viewModel.comeEventToDelete.value = comeEvent
        viewModel.comeEventPosition.value = position
        deleteComeEventDialog.show()
    }
}