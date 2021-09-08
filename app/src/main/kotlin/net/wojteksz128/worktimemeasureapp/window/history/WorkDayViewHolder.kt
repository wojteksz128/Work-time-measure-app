package net.wojteksz128.worktimemeasureapp.window.history

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.FunctionWithParameter
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner

internal class WorkDayViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

    private val mDateTV: TextView = mView.findViewById(R.id.day_label)
    private val mWorkDurationTV: TextView = mView.findViewById(R.id.day_work_duration)
    private val mEventsListLayout: LinearLayout = mView.findViewById(R.id.day_events_list)
    private val secondRunner = PeriodicOperationRunner<WorkDayEvents>()

    fun bind(workDay: WorkDayEvents, updateAction: FunctionWithParameter<WorkDayEvents>) {
        fillDateLabel(workDay)
        fillDurationLabel(workDay)

        prepareListWithEvents(workDay.events)
        prepareCountingIfAnyEventNotFinished(workDay, updateAction)
    }

    private fun fillDurationLabel(workDay: WorkDayEvents) {
        val duration = DateTimeUtils.formatCounterTime(DateTimeUtils.mergeComeEventsDuration(workDay))
        mWorkDurationTV.text = duration
    }

    private fun fillDateLabel(workDay: WorkDayEvents) {
        val dateLabel = DateTimeUtils.formatDate(mView.context.getString(R.string.history_work_day_label_format), workDay.workDay.date)
        mDateTV.text = dateLabel
    }

    private fun prepareListWithEvents(events: List<ComeEvent>) {
        val context = mView.context
        val inflater = LayoutInflater.from(context)

        mEventsListLayout.removeAllViews()

        for (event in events) {
            val eventViewHolder = ComeEventViewHolder(
                inflater.inflate(
                    R.layout.history_day_event_list_item,
                    mEventsListLayout,
                    false
                )
            )
            eventViewHolder.bind(event)
            mEventsListLayout.addView(eventViewHolder.view)
        }
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
