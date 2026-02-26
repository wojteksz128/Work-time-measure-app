package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.util.fixtures.TestFixtures.DEFAULT_DATE
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

/**
 * Central source of default dates and times used across all tests.
 *
 * Using a fixed reference point makes tests deterministic and avoids timezone-related issues.
 * All builders use these defaults unless overridden explicitly.
 *
 * Reference point: 2024-01-15 (Monday), 08:00 UTC
 */
object TestFixtures {

    /** Default date used in tests: a regular Monday. */
    val DEFAULT_DATE: LocalDate = LocalDate.of(2024, 1, 15)

    /** Default work start time: 08:00 UTC on [DEFAULT_DATE]. */
    val DEFAULT_START_TIME: ZonedDateTime =
        ZonedDateTime.of(DEFAULT_DATE, LocalTime.of(8, 0), ZoneOffset.UTC)

    /** Default work end time: 16:00 UTC on [DEFAULT_DATE] (8-hour day). */
    val DEFAULT_END_TIME: ZonedDateTime =
        ZonedDateTime.of(DEFAULT_DATE, LocalTime.of(16, 0), ZoneOffset.UTC)

    /** Default "now" used in tests when current time matters but no event is ongoing. */
    val DEFAULT_NOW: ZonedDateTime = DEFAULT_START_TIME
}

