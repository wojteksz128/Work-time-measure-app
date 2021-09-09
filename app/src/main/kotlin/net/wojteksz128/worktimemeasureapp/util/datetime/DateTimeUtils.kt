package net.wojteksz128.worktimemeasureapp.util.datetime

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
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

    fun formatCounterTime(duration: Duration?): String =
        formatCounterTime(duration, R.string.empty_time_string)

    @Suppress("MemberVisibilityCanBePrivate")
    fun formatCounterTime(duration: Duration?, @StringRes defaultValueResId: Int): String =
        formatCounterTime(duration, WorkTimeMeasureApp.context.getString(defaultValueResId))

    @Suppress("MemberVisibilityCanBePrivate")
    fun formatCounterTime(duration: Duration?, defaultValue: String): String {
        return duration?.let {
            val hours = abs(it.toHours()).toInt()
            val minutes = abs(it.toMinutesPart())
            val seconds = abs(it.toSecondsPart())
            val sign =
                if (it.isNegative && (hours != 0 || minutes != 0 || seconds != 0)) "-" else ""

            "${sign}${hours}:${if (minutes < 10) "0" else ""}${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
        } ?: defaultValue
    }
}