package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.FragmentWorkDayDetailsBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.getLong("workDayId")?.let { workDayId ->
            viewModel.workDayId = workDayId
        }

        comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)
    }

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
            initializeLogic(workDayDetailsComeEvents)
        }
        viewModel.workDay.observe(viewLifecycleOwner) {
            comeEventsAdapter.submitList(it.events)
        }
        return binding.root
    }

    private fun initializeLogic(workDayDetailsComeEvents: RecyclerView) {
        val swipeLeft = RecyclerLeftSwipeActionParam(
            R.color.teal_200,
            R.drawable.ic_baseline_edit_24,
            requireContext()
        ) {
            Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
        }
        val swipeRight = RecyclerRightSwipeActionParam(
            R.color.colorAlert,
            R.drawable.ic_baseline_delete_24,
            requireContext()
        ) {
            Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
        }
        val recyclerSwipeHelper = RecyclerSwipeHelper(comeEventsAdapter, swipeLeft, swipeRight)
        val itemTouchHelper = ItemTouchHelper(recyclerSwipeHelper)
        itemTouchHelper.attachToRecyclerView(workDayDetailsComeEvents)
    }
}