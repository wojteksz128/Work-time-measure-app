package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.databinding.ListItemHistoryWorkDayBinding
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.livedata.RecyclerViewPeriodicUpdater
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewItemClick

class WorkDayAdapter(
    private val context: Context,
    private val dateTimeUtils: DateTimeUtils,
    override var onItemClickListenerProvider: (WorkDay) -> (View) -> Unit = { {} },
) : PagingDataAdapter<WorkDay, WorkDayAdapter.WorkDayViewHolder>(WorkDayEventsDiffCallback),
    RecyclerViewItemClick<WorkDay> {
    private val periodicUpdater = RecyclerViewPeriodicUpdater(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ListItemHistoryWorkDayBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(binding, context, dateTimeUtils)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setOnClickListener(onItemClickListenerProvider(it))
            holder.bind(it)
        }
    }

    override fun onViewAttachedToWindow(holder: WorkDayViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.binding.workDay?.isAllEventsEnded() != true) {
            periodicUpdater.addItem(holder.absoluteAdapterPosition)
            holder.syncUpdaterWith(periodicUpdater)
        }
    }

    override fun onViewDetachedFromWindow(holder: WorkDayViewHolder) {
        periodicUpdater.removeItem(holder.absoluteAdapterPosition)
        super.onViewDetachedFromWindow(holder)
    }


    class WorkDayViewHolder(
        val binding: ListItemHistoryWorkDayBinding,
        context: Context,
        dateTimeUtils: DateTimeUtils,
    ) : RecyclerView.ViewHolder(binding.root), ClassTagAware {

        private val comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)

        init {
            binding.dateTimeUtils = dateTimeUtils
            binding.dayEventsList.apply {
                adapter = comeEventsAdapter
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically() = false
                }
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }

        fun bind(workDay: WorkDay) {
            binding.workDay = workDay

            comeEventsAdapter.submitList(workDay.events)
        }

        fun syncUpdaterWith(anotherUpdater: RecyclerViewPeriodicUpdater) {
            comeEventsAdapter.syncUpdaterWith(anotherUpdater)
        }

        fun setOnClickListener(onItemClickListener: (View) -> Unit) {
            itemView.setOnClickListener(onItemClickListener)
            binding.dayEventsList.setOnClickListener(onItemClickListener)
            comeEventsAdapter.onItemClickListenerProvider = { onItemClickListener }
        }
    }


    object WorkDayEventsDiffCallback : DiffUtil.ItemCallback<WorkDay>() {

        override fun areItemsTheSame(oldItem: WorkDay, newItem: WorkDay): Boolean {
            return oldItem.id!! == newItem.id!!
        }

        override fun areContentsTheSame(oldItem: WorkDay, newItem: WorkDay) =
            oldItem == newItem
    }
}
