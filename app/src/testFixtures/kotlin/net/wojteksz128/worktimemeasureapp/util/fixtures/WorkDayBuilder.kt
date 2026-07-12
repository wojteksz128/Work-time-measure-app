package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.atEndOfDay
import net.wojteksz128.worktimemeasureapp.util.datetime.atStartOfDay
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

/**
 * Builder for [WorkDay] test objects.
 *
 * Usage:
 * ```kotlin
 * // minimal — all defaults
 * val workDay = aWorkDay()
 *
 * // with a specific date
 * val workDay = aWorkDay { date = LocalDate.of(2024, 3, 10) }
 *
 * // with events
 * val event = aComeEvent { workDayId = 1L }
 * val workDay = aWorkDay(id = 1L) { events(event) }
 *
 * // multi-event day on a specific date
 * val workDay = aWorkDay(id = 2L) {
 *     date = TestFixtures.DEFAULT_DATE.plusDays(1)
 *     events(
 *         aComeEvent { startDate = TestFixtures.DEFAULT_START_TIME },
 *         aComeEvent { startDate = TestFixtures.DEFAULT_START_TIME.plusHours(1) },
 *     )
 * }
 * ```
 */
fun aWorkDay(
    id: Long = 1L,
    block: WorkDayBuilder.() -> Unit = {},
): WorkDay = WorkDayBuilder(id).apply(block).build()

class WorkDayBuilder(private val id: Long = 1L) {

    var date: LocalDate = TestFixtures.DEFAULT_DATE
    var beginSlot: ZonedDateTime? = null
    var endSlot: ZonedDateTime? = null
    private val _events: MutableList<ComeEvent> = mutableListOf()

    /** Appends one or more [ComeEvent]s to this work day. */
    fun events(vararg events: ComeEvent) {
        _events += events
    }

    /** Appends a list of [ComeEvent]s to this work day. */
    fun events(events: List<ComeEvent>) {
        _events += events
    }

    fun build(): WorkDay = WorkDay(
        id = id,
        date = date,
        beginSlot = beginSlot ?: date.atStartOfDay,
        endSlot = endSlot ?: date.atEndOfDay,
        events = _events,
    )
}

