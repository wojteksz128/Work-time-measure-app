package net.wojteksz128.worktimemeasureapp.window.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp.Companion.context
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.databinding.HistoryWorkDayListItemBinding
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.livedata.RecyclerViewPeriodicUpdater

class WorkDayAdapter :
    PagingDataAdapter<WorkDayEvents, WorkDayAdapter.WorkDayViewHolder>(WorkDayEventsDiffCallback) {
    private val periodicUpdater = RecyclerViewPeriodicUpdater(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = HistoryWorkDayListItemBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onViewAttachedToWindow(holder: WorkDayViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.binding.workDay?.hasEventsEnded() != true) {
            periodicUpdater.addItem(holder.absoluteAdapterPosition)
            holder.syncUpdaterWith(periodicUpdater)
        }
    }

    override fun onViewDetachedFromWindow(holder: WorkDayViewHolder) {
        periodicUpdater.removeItem(holder.absoluteAdapterPosition)
        super.onViewDetachedFromWindow(holder)
    }


    class WorkDayViewHolder(val binding: HistoryWorkDayListItemBinding) :
        RecyclerView.ViewHolder(binding.root), ClassTagAware {

        private val comeEventsAdapter = ComeEventsAdapter()

        init {
            binding.dayEventsList.adapter = comeEventsAdapter
            binding.dayEventsList.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically() = false
            }
        }

        fun bind(workDay: WorkDayEvents) {
            binding.workDay = workDay

            comeEventsAdapter.submitList(workDay.events)
        }

        fun syncUpdaterWith(anotherUpdater: RecyclerViewPeriodicUpdater) {
            comeEventsAdapter.syncUpdaterWith(anotherUpdater)
        }
    }


    object WorkDayEventsDiffCallback : DiffUtil.ItemCallback<WorkDayEvents>() {

        override fun areItemsTheSame(oldItem: WorkDayEvents, newItem: WorkDayEvents): Boolean {
            return oldItem.workDay.id!! == newItem.workDay.id!!
        }

        override fun areContentsTheSame(oldItem: WorkDayEvents, newItem: WorkDayEvents) =
            oldItem == newItem
    }
}
