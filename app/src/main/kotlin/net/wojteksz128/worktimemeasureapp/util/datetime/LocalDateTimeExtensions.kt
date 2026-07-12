package net.wojteksz128.worktimemeasureapp.util.datetime

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

fun LocalDateTime.isTheSameDay(other: LocalDateTime?): Boolean =
    other?.let { this.toLocalDate() == it.toLocalDate() }
        ?: (this.toLocalDate() == LocalDate.now())

fun LocalDateTime.withAmPm(amPm: AmPm): LocalDateTime {
    val hour24Format = convert12To24HourFormat(this.hour, amPm)
    return this.withHour(hour24Format)
}