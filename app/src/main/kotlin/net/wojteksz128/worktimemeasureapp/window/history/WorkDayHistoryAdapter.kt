package net.wojteksz128.worktimemeasureapp.window.history

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.databinding.ListItemWorkDayHistoryBinding
import net.wojteksz128.worktimemeasureapp.databinding.ListItemWorkDayHistoryChangeBinding

class WorkDayHistoryAdapter :
    ListAdapter<HistoryDisplayItem, WorkDayHistoryAdapter.WorkDayHistoryViewHolder>(
        HistoryItemDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemWorkDayHistoryBinding.inflate(inflater, parent, false)
        return WorkDayHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkDayHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WorkDayHistoryViewHolder(
        private val binding: ListItemWorkDayHistoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryDisplayItem) {
            binding.apply {
                historyItem = item
                binding.workDayHistoryActionType.setTextColor(
                    ContextCompat.getColor(binding.root.context, item.actionColorRes)
                )
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

    object HistoryItemDiffCallback : DiffUtil.ItemCallback<HistoryDisplayItem>() {
        override fun areItemsTheSame(oldItem: HistoryDisplayItem, newItem: HistoryDisplayItem) =
            oldItem.timestamp == newItem.timestamp && oldItem.entityText == newItem.entityText

        override fun areContentsTheSame(oldItem: HistoryDisplayItem, newItem: HistoryDisplayItem) =
            oldItem == newItem
    }
}