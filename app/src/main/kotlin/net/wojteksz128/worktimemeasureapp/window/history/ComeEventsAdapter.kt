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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ListItemHistoryDayEventBinding
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.duration
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.isEnded
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.isEndingOnTheSameDay
import net.wojteksz128.worktimemeasureapp.util.recyclerView.RecyclerViewItemClick
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

class ComeEventsAdapter(
    private val dateTimeUtils: DateTimeUtils,
    private val lifecycleOwner: LifecycleOwner,
    private val ticker: Flow<Unit>,
    override var onItemClickListenerProvider: (ComeEvent) -> (View) -> Unit = { {} },
) : ListAdapter<ComeEvent, ComeEventsAdapter.ComeEventViewHolder>(ComeEventDiffCallback),
    RecyclerViewItemClick<ComeEvent> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemHistoryDayEventBinding.inflate(inflater, parent, false)
            .apply {
                lifecycleOwner = this@ComeEventsAdapter.lifecycleOwner
            }
        return ComeEventViewHolder(binding, dateTimeUtils, ticker)
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


    class ComeEventViewHolder(
        val binding: ListItemHistoryDayEventBinding,
        private val dateTimeUtils: DateTimeUtils,
        private val ticker: Flow<Unit>,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var updateJob: Job? = null

        fun bind(comeEvent: ComeEvent) {
            val dateFormatId =
                if (comeEvent.isEndingOnTheSameDay) R.string.history_day_event_time_short_format
                else R.string.history_day_event_time_long_format

            binding.comeEvent = ComeEventObject(
                comeEvent,
                { dateTime -> dateTimeUtils.formatDate(dateFormatId, dateTime) },
                { duration -> dateTimeUtils.formatCounterTime(duration) }
            )

            updateJob?.cancel()

            if (!comeEvent.isEnded) {
                updateJob = binding.lifecycleOwner?.lifecycleScope?.launch {
                    ticker.collectLatest {
                        binding.invalidateAll()
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

data class ComeEventObject(
    val entity: ComeEvent,
    val dateConverter: (ZonedDateTime?) -> String?,
    val durationConverter: (Duration) -> String,
) {
    val startDate: String
        get() = dateConverter(entity.startDate)!!

    val finishDate: String?
        get() = dateConverter(entity.endDate)

    val duration: String
        get() = durationConverter(entity.duration)
}
