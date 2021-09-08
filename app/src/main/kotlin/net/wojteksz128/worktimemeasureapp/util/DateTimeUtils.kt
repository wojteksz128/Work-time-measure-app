package net.wojteksz128.worktimemeasureapp.util

import android.annotation.SuppressLint
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object DateTimeUtils {

    fun formatDate(format: String, date: Date?) = date?.let { formatDate(format, date, TimeZone.getDefault()) } ?: ""

    fun formatDate(format: String, date: Date, timeZone: TimeZone = TimeZone.getDefault()): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(format)
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun calculateDuration(comeEvent: ComeEvent): Duration {
        val startTime = comeEvent.startDate.time
        val endTime =
            if (comeEvent.endDate != null) comeEvent.endDate!!.time else DateTimeProvider.currentTime.time
        return Duration.between(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime + 1)) // +1, but exclusive duration
    }

    fun mergeComeEventsDuration(workDay: WorkDayEvents): Duration = workDay.events.map { it.duration }
        .fold(Duration.ZERO) { sum, element -> sum + element }

    // TODO: 30.08.2021 Zmień typ czasu
    fun formatCounterTime(duration: Duration?): String {
        return duration?.let {
            val hours = abs(it.toHours()).toInt()
            val minutes = abs(it.toMinutesPart())
            val seconds = abs(it.toSecondsPart())
            val sign = if (it.isNegative && (hours != 0 || minutes != 0 || seconds != 0)) "-" else ""

            "${sign}${hours}:${if (minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
        } ?: "0:00:00" // TODO: 08.09.2021 As default value
    }

}
