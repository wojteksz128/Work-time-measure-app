package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import org.threeten.bp.LocalDate

/**
 * Builder for [DayOff] test objects.
 *
 * Usage:
 * ```kotlin
 * // minimal — single-day public holiday
 * val dayOff = aDayOff()
 *
 * // custom type and date range
 * val dayOff = aDayOff(id = 2L) {
 *     type = DayOffType.PersonalHoliday
 *     name = "Vacation"
 *     startDate = LocalDate.of(2024, 8, 1)
 *     finishDate = LocalDate.of(2024, 8, 14)
 * }
 *
 * // manually inserted
 * val dayOff = aDayOff { source = DayOffSource.ManualInserted }
 * ```
 */
fun aDayOff(
    id: Long = 1L,
    block: DayOffBuilder.() -> Unit = {},
): DayOff = DayOffBuilder(id).apply(block).build()

class DayOffBuilder(private val id: Long = 1L) {

    var uuid: String? = "uuid-$id"
    var type: DayOffType = DayOffType.PublicHoliday
    var name: String = "Holiday"
    var startDate: LocalDate = TestFixtures.DEFAULT_DATE
    var finishDate: LocalDate = TestFixtures.DEFAULT_DATE
    var source: DayOffSource = DayOffSource.ExternalAPI

    fun build(): DayOff = DayOff(
        id = id,
        uuid = uuid,
        type = type,
        name = name,
        startDate = startDate,
        finishDate = finishDate,
        source = source,
    )
}

