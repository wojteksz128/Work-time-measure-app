package net.wojteksz128.worktimemeasureapp.util.datetime

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.jdk8.DefaultInterfaceTemporal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.math.abs

class DateTimeUtils (
    private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
) {

    fun formatDate(format: String, date: Date?) =
        date?.let { formatDate(format, date, TimeZone.getDefault()) } ?: ""

    fun formatDate(format: String, date: DefaultInterfaceTemporal?) =
        date?.let { formatDate(format, date, ZoneId.systemDefault()) }

    fun formatDate(format: String, date: ZonedDateTime?): String =
        date?.let {
            DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault()).format(it)
        } ?: ""

    private fun formatDate(
        format: String,
        date: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): String {
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(format)
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    private fun formatDate(
        format: String,
        date: DefaultInterfaceTemporal,
        timeZone: ZoneId = ZoneId.systemDefault(),
    ): String {
        val formatter = DateTimeFormatter.ofPattern(format).withZone(timeZone)
        return formatter.format(date)
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

    val ComeEvent?.duration: Duration
        get() = this?.let {
            Duration.between(
                it.startDate,
                it.endDate ?: dateTimeProvider.currentTime
            )
        } ?: Duration.ZERO

}

operator fun Date.minus(other: Date): Date =
    Date(this.time - other.time)

fun Date.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()

fun ZonedDateTime.toDate(): Date = Date(this.toInstant().toEpochMilli())

fun LocalDateTime.isTheSameDay(other: LocalDateTime?): Boolean =
    other?.let { this.toLocalDate() == it.toLocalDate() }
        ?: (this.toLocalDate() == LocalDate.now())

fun ZonedDateTime.isTheSameDay(other: ZonedDateTime?): Boolean =
    other?.let { this.toLocalDate() == it.toLocalDate() }
        ?: (this.toLocalDate() == LocalDate.now())

fun Date.toZonedDateTime(): ZonedDateTime =
    Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault())