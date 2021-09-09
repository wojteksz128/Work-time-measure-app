package net.wojteksz128.worktimemeasureapp.window.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.databinding.HistoryDayEventListItemBinding

class ComeEventsAdapter :
    ListAdapter<ComeEvent, ComeEventsAdapter.ComeEventViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryDayEventListItemBinding.inflate(inflater, parent, false)
        return ComeEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComeEventViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class ComeEventViewHolder(private val binding: HistoryDayEventListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comeEvent: ComeEvent) {
            binding.comeEvent = comeEvent
        }
    }


    object DiffCallback : DiffUtil.ItemCallback<ComeEvent>() {
        override fun areItemsTheSame(oldItem: ComeEvent, newItem: ComeEvent): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ComeEvent, newItem: ComeEvent): Boolean {
            return oldItem.id == newItem.id
        }

    }
}
