package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDayDetailsBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerLeftSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerRightSwipeActionParam
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerSwipeHelper
import javax.inject.Inject

@AndroidEntryPoint
class WorkDayDetailsFragment : Fragment() {
    private val viewModel: WorkDayDetailsViewModel by viewModels()

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var settings: Settings

    private lateinit var binding: FragmentWorkDayDetailsBinding
    private lateinit var comeEventsAdapter: ComeEventsAdapter
    private lateinit var deleteComeEventDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getLong("workDayId")?.let { workDayId ->
            viewModel.workDayId = workDayId
        }

        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)

        deleteComeEventDialog = prepareDeleteComeEventDialog()
    }

    private fun prepareDeleteComeEventDialog() = AlertDialog.Builder(requireContext()).apply {
        setTitle(R.string.work_day_details_come_events_action_delete_title)
        setPositiveButton(R.string.work_day_details_come_events_action_delete) { _, _ ->
            viewModel.onComeEventDelete()
            viewModel.comeEventPosition.value?.let { comeEventsAdapter.notifyItemRemoved(it) }
        }
        setNegativeButton(R.string.work_day_details_come_events_action_cancel) { _, _ ->
        }
    }.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkDayDetailsBinding.inflate(layoutInflater, container, false)
        binding.apply {
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
        viewModel.apply {
            workDay.observe(viewLifecycleOwner) {
                comeEventsAdapter.submitList(it.events)
            }
            comeEventToDelete.observe(viewLifecycleOwner) {
                val message =
                    getString(
                        R.string.work_day_details_come_events_action_delete_message,
                        dateTimeUtils.formatDate(
                            getString(R.string.history_day_event_time_format),
                            it.startDate
                        ),
                        dateTimeUtils.formatDate(
                            getString(R.string.history_day_event_time_format),
                            it.endDate
                        )
                    )
                deleteComeEventDialog.setMessage(message)
            }
        }

        return binding.root
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
        Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
    }

    private fun processDeleteComeEvent(comeEvent: ComeEvent, position: Int) {
        viewModel.comeEventToDelete.value = comeEvent
        viewModel.comeEventPosition.value = position
        deleteComeEventDialog.show()
    }
}