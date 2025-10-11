package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.databinding.ListItemHistoryWorkDayBinding
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewItemClick
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewSwipeCallback
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.util.button.ExpandViewModel
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerLeftSwipeActionParams
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerRightSwipeActionParams

class WorkDayAdapter(
    private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val dateTimeUtils: DateTimeUtils,
    private val lifecycleOwner: LifecycleOwner,
    private val ticker: Flow<Unit>,
    private val workDayItemListener: WorkDayItemListener,
) : PagingDataAdapter<WorkDay, WorkDayAdapter.WorkDayViewHolder>(WorkDayEventsDiffCallback),
    RecyclerViewItemClick<WorkDay> {

    @Suppress("UNUSED_PARAMETER")
    override var onItemClickListenerProvider: (WorkDay) -> (View) -> Unit
        get() = workDayItemListener::onWorkDayClicked
        set(value) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemHistoryWorkDayBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(
            binding,
            context,
            lifecycleOwner,
            dateTimeProvider,
            dateTimeUtils,
            ticker,
            workDayItemListener::onWorkDayEventSelected
        )
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let { workDay ->
            holder.bind(workDay, workDayItemListener.onWorkDayItemViewModelRequires(workDay))
            holder.setOnClickListener(onItemClickListenerProvider(workDay))
        }
    }


    class WorkDayViewHolder(
        val binding: ListItemHistoryWorkDayBinding,
        context: Context,
        private val lifecycleOwner: LifecycleOwner,
        dateTimeProvider: DateTimeProvider,
        private val dateTimeUtils: DateTimeUtils,
        private val ticker: Flow<Unit>,
        private val onEventSwiped: (ComeEventViewHolder, RecyclerViewSwipeCallback.Direction) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root), ClassTagAware {
        private val comeEventsAdapter =
            ComeEventsAdapter(dateTimeProvider, dateTimeUtils, lifecycleOwner, ticker)
        private var updateJob: Job? = null

        init {
            binding.apply {
                lifecycleOwner = this@WorkDayViewHolder.lifecycleOwner
                dateTimeUtils = this@WorkDayViewHolder.dateTimeUtils
                dayEventsList.apply {
                    adapter = comeEventsAdapter
                    layoutManager = object : LinearLayoutManager(context) {
                        override fun canScrollVertically() = false
                    }
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                }
                val rvTouchCallback = RecyclerViewSwipeCallback(
                    ComeEventRecyclerLeftSwipeActionParams(context),
                    ComeEventRecyclerRightSwipeActionParams(context),
                    onEventSwiped
                )
                ItemTouchHelper(rvTouchCallback).attachToRecyclerView(dayEventsList)
            }
        }

        fun bind(workDay: WorkDay, itemViewModel: WorkDayItemViewModel) {
            binding.workDay = workDay
            binding.expandViewModel = itemViewModel

            comeEventsAdapter.submitList(workDay.events)

            updateJob?.cancel()
            if (!workDay.isWorkFinished()) {
                updateJob = lifecycleOwner.lifecycleScope.launch {
                    ticker.collectLatest {
                        binding.invalidateAll()
                    }
                }
            }
        }

        fun setOnClickListener(onItemClickListener: (View) -> Unit) {
            binding.dayLabelContainer.setOnClickListener(onItemClickListener)
        }
    }


    object WorkDayEventsDiffCallback : DiffUtil.ItemCallback<WorkDay>() {

        override fun areItemsTheSame(oldItem: WorkDay, newItem: WorkDay): Boolean {
            return oldItem.id!! == newItem.id!!
        }

        override fun areContentsTheSame(oldItem: WorkDay, newItem: WorkDay) =
            oldItem == newItem
    }

    interface WorkDayItemListener {
        fun onWorkDayItemViewModelRequires(workDay: WorkDay): WorkDayItemViewModel

        fun onWorkDayEventSelected(
            viewHolder: ComeEventViewHolder,
            direction: RecyclerViewSwipeCallback.Direction,
        )

        fun onWorkDayClicked(workDay: WorkDay): (View) -> Unit = {}
    }

    class WorkDayItemViewModel : ExpandViewModel()
}

