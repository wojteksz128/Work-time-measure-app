package net.wojteksz128.worktimemeasureapp.window.history

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp.Companion.context
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.FunctionWithParameter
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils

internal class WorkDayViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

    private val mDateTV: TextView = mView.findViewById(R.id.day_label)
    private val mWorkDurationTV: TextView = mView.findViewById(R.id.day_work_duration)
    private val mEventsListLayout: RecyclerView = mView.findViewById(R.id.day_events_list)
    private val secondRunner = PeriodicOperationRunner<WorkDayEvents>()
    private val comeEventsAdapter = ComeEventsAdapter()

    init {
        mEventsListLayout.adapter = comeEventsAdapter
        mEventsListLayout.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
    }

    fun bind(workDay: WorkDayEvents, updateAction: FunctionWithParameter<WorkDayEvents>) {
        fillDateLabel(workDay)
        fillDurationLabel(workDay)

        comeEventsAdapter.submitList(workDay.events)
        prepareCountingIfAnyEventNotFinished(workDay, updateAction)
    }

    private fun fillDurationLabel(workDay: WorkDayEvents) {
        val duration =
            DateTimeUtils.formatCounterTime(DateTimeUtils.mergeComeEventsDuration(workDay))
        mWorkDurationTV.text = duration
    }

    private fun fillDateLabel(workDay: WorkDayEvents) {
        val dateLabel =
            DateTimeUtils.formatDate(mView.context.getString(R.string.history_work_day_label_format),
                workDay.workDay.date)
        mDateTV.text = dateLabel
    }

    private fun prepareCountingIfAnyEventNotFinished(
        workDay: WorkDayEvents,
        updateAction: FunctionWithParameter<WorkDayEvents>
    ) {
        if (!workDay.hasEventsEnded()) {
            if (!secondRunner.isRunning) {
                secondRunner.setConsumer(updateAction)
                Log.v(
                    TAG,
                    "prepareCountingIfAnyEventNotFinished: start second updater for day ${workDay.workDay.date}"
                )
                secondRunner.start()
            }
        } else {
            secondRunner.stop()
        }
    }

    companion object {
        private val TAG = WorkDayViewHolder::class.java.simpleName
    }
}
