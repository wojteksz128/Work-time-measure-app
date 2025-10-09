package net.wojteksz128.worktimemeasureapp.util.datetime

import org.threeten.bp.LocalDate

class LocalDateRange(override val start: LocalDate, override val endInclusive: LocalDate) :
    ClosedRange<LocalDate>, Iterable<LocalDate> {

    override fun iterator(): Iterator<LocalDate> {
        return object : Iterator<LocalDate> {
            var next = start

            override fun hasNext(): Boolean = next <= endInclusive

            override fun next(): LocalDate {
                val result = next
                next = next.plusDays(1)
                return result
            }
        }
    }
}

operator fun LocalDate.rangeTo(other: LocalDate) = LocalDateRange(this, other)