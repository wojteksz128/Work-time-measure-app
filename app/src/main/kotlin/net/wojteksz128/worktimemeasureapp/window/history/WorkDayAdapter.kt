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
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import net.wojteksz128.worktimemeasureapp.util.model.extension.workTime
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewSwipeCallback
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerLeftSwipeActionParams
import net.wojteksz128.worktimemeasureapp.window.util.recyclerView.ComeEventRecyclerRightSwipeActionParams

class WorkDayAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val ticker: Flow<Unit>,
    private val workDayItemListener: WorkDayItemListener,
) : PagingDataAdapter<WorkDayItemUiModel, WorkDayAdapter.WorkDayViewHolder>(
    WorkDayEventsDiffCallback
) {

    private val expandedItemIds = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemHistoryWorkDayBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(
            binding,
            lifecycleOwner,
            ticker,
            workDayItemListener,
            context
        )
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val isExpanded = expandedItemIds.contains(item.id)

        holder.bind(item, isExpanded) { clickedId ->
            if (expandedItemIds.contains(clickedId)) {
                expandedItemIds.remove(clickedId)
            } else {
                expandedItemIds.add(clickedId)
            }
            notifyItemChanged(position)
        }
    }


    class WorkDayViewHolder(
        val binding: ListItemHistoryWorkDayBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val ticker: Flow<Unit>,
        private val listener: WorkDayItemListener,
        context: Context,
    ) : RecyclerView.ViewHolder(binding.root), ClassTagAware {

        private val comeEventsAdapter = ComeEventsAdapter(lifecycleOwner, ticker)
        private var updateJob: Job? = null

        init {
            binding.dayEventsList.apply {
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
                listener::onWorkDayEventSelected
            )
            ItemTouchHelper(rvTouchCallback).attachToRecyclerView(binding.dayEventsList)
        }

        fun bind(workDayItem: WorkDayItemUiModel, isExpanded: Boolean, onToggle: (Long) -> Unit) {
            binding.dayLabel.text = workDayItem.dateLabel

            if (isExpanded) {
                binding.dayEventsListContainer.visibility = View.VISIBLE
                binding.dayEventsList.visibility =
                    if (workDayItem.hasEvents) View.VISIBLE else View.INVISIBLE
                binding.dayNoEventsLabel.visibility =
                    if (workDayItem.hasEvents) View.INVISIBLE else View.VISIBLE
            } else {
                binding.dayEventsListContainer.visibility = View.INVISIBLE
            }
            binding.dayExpand.setOnClickListener { onToggle(workDayItem.id) }

            binding.root.setOnClickListener {
                listener.onWorkDayClicked(workDayItem.originalEntity)
            }

            comeEventsAdapter.submitList(workDayItem.events)

            updateJob?.cancel()

            when (workDayItem) {
                is WorkDayItemUiModel.Finished -> {
                    binding.dayWorkDuration.text = workDayItem.formattedDuration
                }

                is WorkDayItemUiModel.Active -> {
                    updateJob = lifecycleOwner.lifecycleScope.launch {
                        ticker.collectLatest {
                            binding.dayWorkDuration.text =
                                workDayItem.originalEntity.workTime.toCounterString()
                        }
                    }
                }
            }
        }
    }


    object WorkDayEventsDiffCallback : DiffUtil.ItemCallback<WorkDayItemUiModel>() {

        override fun areItemsTheSame(
            oldItem: WorkDayItemUiModel,
            newItem: WorkDayItemUiModel,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkDayItemUiModel, newItem: WorkDayItemUiModel) =
            oldItem == newItem
    }

    interface WorkDayItemListener {
        fun onWorkDayEventSelected(
            viewHolder: ComeEventViewHolder,
            direction: RecyclerViewSwipeCallback.Direction,
        )

        fun onWorkDayClicked(workDay: WorkDay)
    }
}

