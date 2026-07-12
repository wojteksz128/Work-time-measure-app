package net.wojteksz128.worktimemeasureapp.util.datetime

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

val LocalDate.atStartOfDay: ZonedDateTime
    get() = this.atStartOfDay(ZoneId.systemDefault())

val LocalDate.atEndOfDay: ZonedDateTime
    get() = this.atStartOfDay.plusDays(1).minus(1, ChronoUnit.MILLIS)

val LocalDate.monthRangeTillToday: LocalDateRange
    get() = (this.withDayOfMonth(1)..this)