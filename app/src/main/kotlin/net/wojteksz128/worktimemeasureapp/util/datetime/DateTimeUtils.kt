package net.wojteksz128.worktimemeasureapp.util.datetime

import android.content.Context
import androidx.annotation.StringRes
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.jdk8.DefaultInterfaceTemporal
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs

open class DateTimeUtils(
    private val context: Context,
    private val dateTimeProvider: DateTimeProvider,
) {

    fun formatDate(format: String, date: DefaultInterfaceTemporal?) =
        date?.let { formatDate(format, date, ZoneId.systemDefault()) }

    fun formatDate(format: String, date: ZonedDateTime?): String =
        date?.let {
            DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault()).format(it)
        } ?: ""

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

    fun getDaysInMonthRangeToDate(date: LocalDate): LocalDateRange =
        (date.withDayOfMonth(1)..date)

    val ComeEvent?.duration: Duration
        get() = this?.let {
            Duration.between(
                it.startDate,
                it.endDate ?: dateTimeProvider.currentTime
            )
        } ?: Duration.ZERO

    companion object {
        fun getStartDayTime(date: LocalDate): ZonedDateTime =
            date.atStartOfDay(ZoneId.systemDefault())

        fun getEndDayTime(date: LocalDate): ZonedDateTime =
            date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).minus(1, ChronoUnit.MILLIS)
    }
}

fun min(a: Duration, b: Duration) = if (a < b) a else b

fun LocalDateTime.isTheSameDay(other: LocalDateTime?): Boolean =
    other?.let { this.toLocalDate() == it.toLocalDate() }
        ?: (this.toLocalDate() == LocalDate.now())

fun LocalDateTime.withAmPm(amPm: AmPm): LocalDateTime {
    val hour24Format = convert12To24HourFormat(this.hour, amPm)
    return this.withHour(hour24Format)
}

fun ZonedDateTime.isTheSameDay(other: ZonedDateTime?): Boolean =
    other?.let { this.toLocalDate() == it.toLocalDate() }
        ?: (this.toLocalDate() == LocalDate.now())

fun convert12To24HourFormat(hour: Int, amPm: AmPm): Int = hour % 12 + when (amPm) {
    AmPm.AM -> 0
    AmPm.PM -> 12
}

fun convert24To12HourFormat(hour: Int): Pair<Int, AmPm> = when (hour) {
    0 -> Pair(12, AmPm.AM)
    in 1..11 -> Pair(hour, AmPm.AM)
    12 -> Pair(12, AmPm.PM)
    in 13..23 -> Pair(hour - 12, AmPm.PM)
    else -> throw IllegalArgumentException("Invalid 24-hour format hour: $hour")
}

enum class AmPm {
    AM, PM
}