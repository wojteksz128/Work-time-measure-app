package net.wojteksz128.worktimemeasureapp.window.history

import android.util.Log
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
import net.wojteksz128.worktimemeasureapp.util.FunctionWithParameter
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

class WorkDayAdapter(private val uiThreadRunner: (Runnable) -> Unit) :
    PagingDataAdapter<WorkDayEvents, WorkDayAdapter.WorkDayViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkDayViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = HistoryWorkDayListItemBinding.inflate(inflater, parent, false)
        return WorkDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkDayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, getUpdateAction(it, position))
        }
    }

    private fun getUpdateAction(
        currentDayEvents: WorkDayEvents,
        position: Int
    ): FunctionWithParameter<WorkDayEvents> {
        return object : FunctionWithParameter<WorkDayEvents>(currentDayEvents) {
            override fun action(obj: WorkDayEvents) {
                uiThreadRunner { notifyItemChanged(position) }
            }
        }
    }


    class WorkDayViewHolder(private val binding: HistoryWorkDayListItemBinding) :
        RecyclerView.ViewHolder(binding.root), ClassTagAware {

        private val secondRunner = PeriodicOperationRunner<WorkDayEvents>()
        private val comeEventsAdapter = ComeEventsAdapter()

        init {
            binding.dayEventsList.adapter = comeEventsAdapter
            binding.dayEventsList.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically() = false
            }
        }

        fun bind(workDay: WorkDayEvents, updateAction: FunctionWithParameter<WorkDayEvents>) {
            binding.workDay = workDay

            comeEventsAdapter.submitList(workDay.events)
            prepareCountingIfAnyEventNotFinished(workDay, updateAction)
        }

        private fun prepareCountingIfAnyEventNotFinished(
            workDay: WorkDayEvents,
            updateAction: FunctionWithParameter<WorkDayEvents>
        ) {
            if (!workDay.hasEventsEnded()) {
                if (!secondRunner.isRunning) {
                    secondRunner.setConsumer(updateAction)
                    Log.v(
                        classTag,
                        "prepareCountingIfAnyEventNotFinished: start second updater for day ${workDay.workDay.date}"
                    )
                    secondRunner.start()
                }
            } else {
                secondRunner.stop()
            }
        }
    }


    object DiffCallback : DiffUtil.ItemCallback<WorkDayEvents>() {

        override fun areItemsTheSame(oldItem: WorkDayEvents, newItem: WorkDayEvents): Boolean {
            return oldItem.workDay.id!! == newItem.workDay.id!!
        }

        override fun areContentsTheSame(oldItem: WorkDayEvents, newItem: WorkDayEvents) =
            oldItem == newItem
    }
}
