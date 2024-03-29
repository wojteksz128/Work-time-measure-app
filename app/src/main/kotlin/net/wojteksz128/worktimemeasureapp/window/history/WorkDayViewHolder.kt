package net.wojteksz128.worktimemeasureapp.window.history

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils
import java.util.*

internal class WorkDayViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

    private val mDateTV: TextView = mView.findViewById(R.id.day_label)
    private val mWorkDurationTV: TextView = mView.findViewById(R.id.day_work_duration)
    private val mEventsListLayout: LinearLayout = mView.findViewById(R.id.day_events_list)

    fun bind(workDay: WorkDayEvents) {
        fillDateLabel(workDay)
        fillDurationLabel(workDay)

        prepareListWithEvents(workDay.events)
    }

    private fun fillDurationLabel(workDay: WorkDayEvents) {
        val duration = DateTimeUtils.formatDate(mView.context.getString(R.string.history_work_day_duration_format),
                DateTimeUtils.mergeComeEventsDuration(workDay), TimeZone.getTimeZone("UTC"))
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
            val eventViewHolder = ComeEventViewHolder(inflater.inflate(R.layout.history_day_event_list_item, mEventsListLayout, false))
            eventViewHolder.bind(event)
            mEventsListLayout.addView(eventViewHolder.view)
        }
    }
}
