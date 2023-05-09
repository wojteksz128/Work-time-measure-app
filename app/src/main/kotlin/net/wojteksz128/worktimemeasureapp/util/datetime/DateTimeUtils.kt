package net.wojteksz128.worktimemeasureapp.util.datetime

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class DateTimeUtils (
    private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
) {

    fun formatDate(format: String, date: Date?) =
        date?.let { formatDate(format, date, TimeZone.getDefault()) } ?: ""

    fun formatDate(format: String, date: Date, timeZone: TimeZone = TimeZone.getDefault()): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(format)
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun calculateDuration(comeEvent: ComeEvent): Duration {
        val startTime = comeEvent.startDate.time
        val endTime =
            if (comeEvent.endDate != null) comeEvent.endDate!!.time else dateTimeProvider.currentTime.time
        return Duration.between(
            Instant.ofEpochMilli(startTime),
            Instant.ofEpochMilli(endTime + 1)
        ) // +1, but exclusive duration
    }

    fun mergeComeEventsDuration(workDay: WorkDay?): Duration = workDay?.events?.map { it.duration }
        ?.fold(Duration.ZERO) { sum, element -> sum + element } ?: Duration.ZERO

    fun formatCounterTime(duration: Duration?): String =
        formatCounterTime(duration, R.string.empty_time_string)

    @Suppress("MemberVisibilityCanBePrivate")
    fun formatCounterTime(duration: Duration?, @StringRes defaultValueResId: Int): String =
        formatCounterTime(duration, context.getString(defaultValueResId))

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

    val ComeEvent.duration: Duration
        get() = Duration.ofMillis(((endDate ?: dateTimeProvider.currentTime) - startDate).time)

}

operator fun Date.minus(other: Date): Date =
    Date(this.time - other.time)

fun toLocalDate(date: Date) =
    Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()