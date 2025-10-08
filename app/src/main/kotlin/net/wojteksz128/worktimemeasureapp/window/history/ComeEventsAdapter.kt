package net.wojteksz128.worktimemeasureapp.window.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.databinding.ListItemHistoryDayEventBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.datetime.isTheSameDay
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewItemClick

class ComeEventsAdapter(
    private val dateTimeUtils: DateTimeUtils,
    private val lifecycleOwner: LifecycleOwner,
    override var onItemClickListenerProvider: (ComeEvent) -> (View) -> Unit = { {} },
) : ListAdapter<ComeEvent, ComeEventsAdapter.ComeEventViewHolder>(ComeEventDiffCallback),
    RecyclerViewItemClick<ComeEvent> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemHistoryDayEventBinding.inflate(inflater, parent, false)
            .apply {
                dateTimeUtils = this@ComeEventsAdapter.dateTimeUtils
                lifecycleOwner = this@ComeEventsAdapter.lifecycleOwner
            }
        return ComeEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComeEventViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
            holder.setOnClickListener(onItemClickListenerProvider(it))
        }
    }

    fun modifyCurrentList(operation: MutableList<ComeEvent>.() -> Unit) {
        val currentList = currentList.toMutableList()
        currentList.operation()
        submitList(currentList)
    }


    class ComeEventViewHolder(val binding: ListItemHistoryDayEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var updateJob: Job? = null

        fun bind(comeEvent: ComeEvent) {
            binding.comeEvent = comeEvent
            binding.endsAtTheSameDay = comeEvent.endDate?.let { endDate ->
                comeEvent.startDate.isTheSameDay(endDate)
            } ?: false

            updateJob?.cancel()

            if (!comeEvent.isEnded) {
                updateJob = binding.lifecycleOwner?.lifecycleScope?.launch {
                    while (isActive) {
                        binding.invalidateAll()
                        delay(1000)
                    }
                }
            }
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
                    oldItem.workDayId == newItem.workDayId &&
                    oldItem.endDate != null
        }

    }
}
