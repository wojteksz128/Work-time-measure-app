package net.wojteksz128.worktimemeasureapp.util

import android.annotation.SuppressLint
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    @Suppress("unused")
    private val TAG = DateTimeUtils::class.java.simpleName

    fun formatDate(format: String, date: Date, timeZone: TimeZone = TimeZone.getDefault()): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(format)
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun calculateDuration(comeEvent: ComeEvent): Date {
        val startTime = comeEvent.startDate.time
        val endTime = if (comeEvent.endDate != null) comeEvent.endDate!!.time else DateTimeProvider.currentTime.time
        return Date(endTime - startTime)
    }

    fun mergeComeEventsDuration(workDay: WorkDayEvents) = Date(workDay.events.sumBy {
        it.duration?.time?.toInt() ?: calculateDuration(it).time.toInt()
    }.toLong())

}
