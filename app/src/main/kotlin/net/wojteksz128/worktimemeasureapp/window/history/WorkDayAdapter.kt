package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.databinding.HistoryWorkDayListItemBinding
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.livedata.RecyclerViewPeriodicUpdater

class WorkDayAdapter(
    private val context: Context,
    private val dateTimeUtils: DateTimeUtils
) : PagingDataAdapter<WorkDay, WorkDayAdapter.WorkDayViewHolder>(WorkDayEventsDiffCallback) {
    private val periodicUpdater = RecyclerViewPeriodicUpdater(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = HistoryWorkDayListItemBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(binding, context, dateTimeUtils)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let {
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
        val binding: HistoryWorkDayListItemBinding,
        context: Context,
        dateTimeUtils: DateTimeUtils
    ) :
        RecyclerView.ViewHolder(binding.root), ClassTagAware {

        private val comeEventsAdapter = ComeEventsAdapter(dateTimeUtils)

        init {
            binding.dateTimeUtils = dateTimeUtils
            binding.dayEventsList.adapter = comeEventsAdapter
            binding.dayEventsList.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically() = false
            }
        }

        fun bind(workDay: WorkDay) {
            binding.workDay = workDay

            comeEventsAdapter.submitList(workDay.events)
        }

        fun syncUpdaterWith(anotherUpdater: RecyclerViewPeriodicUpdater) {
            comeEventsAdapter.syncUpdaterWith(anotherUpdater)
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
