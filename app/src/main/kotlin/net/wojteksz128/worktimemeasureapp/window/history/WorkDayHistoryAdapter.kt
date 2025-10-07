package net.wojteksz128.worktimemeasureapp.window.history

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.databinding.ListItemWorkDayHistoryBinding
import net.wojteksz128.worktimemeasureapp.databinding.ListItemWorkDayHistoryChangeBinding
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils

class WorkDayHistoryAdapter(private val dateTimeUtils: DateTimeUtils) :
    ListAdapter<GroupedHistoryItem, WorkDayHistoryAdapter.WorkDayHistoryViewHolder>(
        GroupedHistoryItemDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemWorkDayHistoryBinding.inflate(inflater, parent, false)
        return WorkDayHistoryViewHolder(binding, dateTimeUtils)
    }

    override fun onBindViewHolder(holder: WorkDayHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WorkDayHistoryViewHolder(
        private val binding: ListItemWorkDayHistoryBinding,
        private val dateTimeUtils: DateTimeUtils,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroupedHistoryItem) {
            binding.apply {
                historyItem = item
                dateTimeUtils = this@WorkDayHistoryViewHolder.dateTimeUtils
                workDayHistoryChanges.removeAllViews()
            }

            item.changes.forEach { itemChange ->
                val inflater = LayoutInflater.from(binding.root.context)
                val binding = ListItemWorkDayHistoryChangeBinding.inflate(
                    inflater,
                    binding.workDayHistoryChanges,
                    true
                )
                binding.apply {
                    change = itemChange
                    workDayHistoryChangeOldValue.paintFlags =
                        workDayHistoryChangeOldValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    if (itemChange.newValue == null) {
                        workDayHistoryChangeNewValue.visibility = View.GONE
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

    object GroupedHistoryItemDiffCallback : DiffUtil.ItemCallback<GroupedHistoryItem>() {
        override fun areItemsTheSame(oldItem: GroupedHistoryItem, newItem: GroupedHistoryItem) =
            oldItem.timestamp == newItem.timestamp && oldItem.entityType == newItem.entityType

        override fun areContentsTheSame(oldItem: GroupedHistoryItem, newItem: GroupedHistoryItem) =
            oldItem == newItem
    }
}