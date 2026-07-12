package net.wojteksz128.worktimemeasureapp.util.datetime

import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

fun ZonedDateTime.isTheSameDay(other: ZonedDateTime?): Boolean =
    other?.let { this.toLocalDate() == it.toLocalDate() }
        ?: (this.toLocalDate() == LocalDate.now())