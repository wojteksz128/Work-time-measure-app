package net.wojteksz128.worktimemeasureapp.util

import android.annotation.SuppressLint
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object DateTimeUtils {

    private val SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1)
    private val MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1)
    private val HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1)

    fun formatDate(format: String, date: Date, timeZone: TimeZone = TimeZone.getDefault()): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(format)
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun calculateDuration(comeEvent: ComeEvent): Date {
        val startTime = comeEvent.startDate.time
        val endTime =
            if (comeEvent.endDate != null) comeEvent.endDate!!.time else DateTimeProvider.currentTime.time
        return Date(endTime - startTime)
    }

    fun mergeComeEventsDuration(workDay: WorkDayEvents) = Date(workDay.events.sumBy {
        it.duration?.time?.toInt() ?: calculateDuration(it).time.toInt()
    }.toLong())

    // TODO: 30.08.2021 Zmień typ czasu
    fun formatCounterTime(timeInMillis: Long): String {
        val hours = abs(timeInMillis / HOUR_IN_MILLIS).toInt()
        val minutes = abs(timeInMillis % HOUR_IN_MILLIS / MINUTE_IN_MILLIS).toInt()
        val seconds = abs(timeInMillis % MINUTE_IN_MILLIS / SECOND_IN_MILLIS).toInt()
        val sign = if (timeInMillis < 0 && (hours != 0 || minutes != 0 || seconds != 0)) "-" else ""

        return "${sign}${hours}:${if (minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
    }

}
