package net.wojteksz128.worktimemeasureapp.util

import android.annotation.SuppressLint
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    @JvmOverloads
    fun formatDate(format: String, date: Date, timeZone: TimeZone = TimeZone.getDefault()): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(format)
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun calculateDuration(comeEvent: ComeEvent): Date {
        val startTime = comeEvent.startDate.time
        val endTime = if (comeEvent.endDate != null) comeEvent.endDate!!.time else System.currentTimeMillis()
        return Date(endTime - startTime)
    }

    fun mergeComeEventsDuration(workDay: WorkDayEvents): Date {
        var millisSum: Long = 0

        for (comeEvent in workDay.events) {
            millisSum += if (comeEvent.duration != null) comeEvent.duration!!.time else calculateDuration(comeEvent).time
        }

        return Date(millisSum)
    }
}
