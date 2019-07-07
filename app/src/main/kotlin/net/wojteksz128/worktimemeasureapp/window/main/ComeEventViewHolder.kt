package net.wojteksz128.worktimemeasureapp.window.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils
import java.util.*

class ComeEventViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val mStartDateTV: TextView = view.findViewById(R.id.main_day_event_start_date)
    private val mEndDateTV: TextView = view.findViewById(R.id.main_day_event_end_date)
    private val mDurationTV: TextView = view.findViewById(R.id.main_day_event_duration)


    fun bind(comeEvent: ComeEvent) {
        fillStartTime(comeEvent)
        fillEndTime(comeEvent)
        fillDuration(comeEvent)
    }

    private fun fillDuration(comeEvent: ComeEvent) {
        val duration = if (comeEvent.duration != null)
            DateTimeUtils.formatDate(view.context.getString(R.string.main_work_day_duration_format),
                    comeEvent.duration!!, TimeZone.getTimeZone("UTC"))
        else
            ""
        mDurationTV.text = duration
    }

    private fun fillEndTime(comeEvent: ComeEvent) {
        val endTime = if (comeEvent.endDate != null) DateTimeUtils.formatDate(view.context.getString(R.string.main_day_event_time_format), comeEvent.endDate!!) else "Teraz"
        mEndDateTV.text = endTime
    }

    private fun fillStartTime(comeEvent: ComeEvent) {
        val startTime = DateTimeUtils.formatDate(view.context.getString(R.string.main_day_event_time_format), comeEvent.startDate)
        mStartDateTV.text = startTime
    }
}
