package net.wojteksz128.worktimemeasureapp.window.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.databinding.HistoryDayEventListItemBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.livedata.RecyclerViewPeriodicUpdater

class ComeEventsAdapter :
    ListAdapter<ComeEvent, ComeEventsAdapter.ComeEventViewHolder>(ComeEventDiffCallback) {
    private val periodicUpdater = RecyclerViewPeriodicUpdater(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryDayEventListItemBinding.inflate(inflater, parent, false)
        return ComeEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComeEventViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onViewAttachedToWindow(holder: ComeEventViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.binding.comeEvent?.isEnded != true) {
            periodicUpdater.addItem(holder.absoluteAdapterPosition)
        }
    }

//    override fun onViewDetachedFromWindow(holder: ComeEventViewHolder) {
        // TODO: 22.09.2021 Jak to rozwiązać na dashboard 
//        periodicUpdater.removeItem(holder.absoluteAdapterPosition)
//        super.onViewDetachedFromWindow(holder)
//    }

    fun syncUpdaterWith(anotherRunner: PeriodicOperation.PeriodicOperationRunner) {
        periodicUpdater.syncWith(anotherRunner)
    }

    fun syncUpdaterWith(anotherUpdater: RecyclerViewPeriodicUpdater) {
        periodicUpdater.syncWith(anotherUpdater)
    }


    class ComeEventViewHolder(val binding: HistoryDayEventListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comeEvent: ComeEvent) {
            binding.comeEvent = comeEvent
        }
    }


    object ComeEventDiffCallback : DiffUtil.ItemCallback<ComeEvent>() {
        override fun areItemsTheSame(oldItem: ComeEvent, newItem: ComeEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ComeEvent, newItem: ComeEvent): Boolean {
            return oldItem.startDate == newItem.startDate &&
                    oldItem.endDate == newItem.endDate &&
                    oldItem.durationMillis == newItem.durationMillis &&
                    oldItem.workDayId == newItem.workDayId &&
                    oldItem.endDate != null
        }

    }
}
