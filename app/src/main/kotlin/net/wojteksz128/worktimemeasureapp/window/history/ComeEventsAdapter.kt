package net.wojteksz128.worktimemeasureapp.window.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.databinding.HistoryDayEventListItemBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.coroutines.PeriodicOperation
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.livedata.RecyclerViewPeriodicUpdater
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewItemClick

class ComeEventsAdapter(
    private val dateTimeUtils: DateTimeUtils,
    override var onItemClickListenerProvider: (ComeEvent) -> (View) -> Unit = { {} }
) : ListAdapter<ComeEvent, ComeEventsAdapter.ComeEventViewHolder>(ComeEventDiffCallback),
    RecyclerViewItemClick<ComeEvent> {
    private val periodicUpdater = RecyclerViewPeriodicUpdater(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryDayEventListItemBinding.inflate(inflater, parent, false)
            .apply {
                dateTimeUtils = this@ComeEventsAdapter.dateTimeUtils
            }
        return ComeEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComeEventViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
            holder.setOnClickListener(onItemClickListenerProvider(it))
        }
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

        fun setOnClickListener(onItemClickListener: (View) -> Unit) {
            itemView.setOnClickListener(onItemClickListener)
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
