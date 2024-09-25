package net.wojteksz128.worktimemeasureapp.database.workDay

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

object WorkDayUtils {

    fun calculateBeginSlot(date: ZonedDateTime): ZonedDateTime = date.truncatedTo(ChronoUnit.DAYS)

    fun calculateEndSlot(date: ZonedDateTime): ZonedDateTime =
        date.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusNanos(1)
}
