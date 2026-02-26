package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import org.threeten.bp.ZonedDateTime

/**
 * Builder for [ComeEvent] test objects.
 *
 * Usage:
 * ```kotlin
 * // minimal — open event (no end)
 * val event = aComeEvent()
 *
 * // closed event with explicit times
 * val event = aComeEvent {
 *     startDate = TestFixtures.DEFAULT_START_TIME
 *     endDate = TestFixtures.DEFAULT_START_TIME.plusHours(8)
 * }
 *
 * // tied to a specific work day
 * val event = aComeEvent(id = 2L) { workDayId = 5L }
 * ```
 */
fun aComeEvent(
    id: Long = 1L,
    block: ComeEventBuilder.() -> Unit = {},
): ComeEvent = ComeEventBuilder(id).apply(block).build()

class ComeEventBuilder(private val id: Long = 1L) {

    var startDate: ZonedDateTime = TestFixtures.DEFAULT_START_TIME
    var endDate: ZonedDateTime? = TestFixtures.DEFAULT_END_TIME
    var workDayId: Long = 1L

    /** Convenience: marks the event as still in progress (no end time). */
    fun inProgress() {
        endDate = null
    }

    fun build(): ComeEvent = ComeEvent(
        id = id,
        startDate = startDate,
        endDate = endDate,
        workDayId = workDayId,
    )
}

