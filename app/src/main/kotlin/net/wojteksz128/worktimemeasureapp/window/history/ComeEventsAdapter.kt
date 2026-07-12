package net.wojteksz128.worktimemeasureapp.window.history

import android.view.LayoutInflater
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
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.duration

class ComeEventsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val ticker: Flow<Unit>,
    private var onItemClick: (ComeEvent) -> Unit = {},
) : ListAdapter<ComeEventItemUiModel, ComeEventsAdapter.ComeEventViewHolder>(ComeEventDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComeEventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemHistoryDayEventBinding.inflate(inflater, parent, false)
        return ComeEventViewHolder(binding, ticker, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: ComeEventViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, onItemClick)
    }


    class ComeEventViewHolder(
        val binding: ListItemHistoryDayEventBinding,
        private val ticker: Flow<Unit>,
        private val lifecycleOwner: LifecycleOwner,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var updateJob: Job? = null

        fun bind(comeEventItem: ComeEventItemUiModel, onClick: (ComeEvent) -> Unit) {
            binding.root.setOnClickListener { onClick(comeEventItem.originalEntity) }

            binding.historyDayEventStartDate.text = comeEventItem.startDateLabel

            updateJob?.cancel()

            when (comeEventItem) {
                is ComeEventItemUiModel.Finished -> {
                    binding.historyDayEventEndDate.text = comeEventItem.finishDateLabel
                    binding.historyDayEventDuration.text = comeEventItem.formattedDuration
                }

                is ComeEventItemUiModel.Active -> {
                    binding.historyDayEventEndDate.text =
                        binding.root.context.getString(R.string.history_day_event_end_date_now)
                    binding.historyDayEventDuration.text =
                        comeEventItem.originalEntity.duration.toCounterString()

                    updateJob = lifecycleOwner.lifecycleScope.launch {
                        ticker.collectLatest {
                            binding.historyDayEventDuration.text =
                                comeEventItem.originalEntity.duration.toCounterString()
                        }
                    }
                }
            }
        }
    }


    object ComeEventDiffCallback : DiffUtil.ItemCallback<ComeEventItemUiModel>() {
        override fun areItemsTheSame(
            oldItem: ComeEventItemUiModel,
            newItem: ComeEventItemUiModel,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ComeEventItemUiModel,
            newItem: ComeEventItemUiModel,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
