package net.wojteksz128.worktimemeasureapp.util.fixtures

import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

/**
 * Lightweight, Hilt-free controllable clock for JVM unit tests.
 *
 * For instrumented (Android) tests, use [net.wojteksz128.worktimemeasureapp.util.FakeDateTimeProvider]
 * which extends [net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider] and integrates
 * with the Hilt DI graph.
 *
 * Usage:
 * ```kotlin
 * val clock = TestClock()
 *
 * // read current time
 * val now = clock.now
 *
 * // set an exact moment
 * clock.setTime(ZonedDateTime.parse("2024-06-01T09:00:00Z"))
 *
 * // advance by a duration
 * clock.advance(Duration.ofHours(2))
 *
 * // use in a JUnit rule for automatic reset
 * @get:Rule val clockRule = TestClockRule()
 * ```
 */
class TestClock(initialTime: ZonedDateTime = TestFixtures.DEFAULT_NOW) {

    private var _now: ZonedDateTime = initialTime

    /** Returns the current simulated time. */
    val now: ZonedDateTime get() = _now

    /** Returns the current simulated date. */
    val today: LocalDate get() = _now.toLocalDate()

    /** Returns the current simulated time zone. */
    val zone: ZoneId get() = _now.zone

    /** Sets the clock to an exact [dateTime]. */
    fun setTime(dateTime: ZonedDateTime) {
        _now = dateTime
    }

    /** Advances the clock by [duration]. */
    fun advance(duration: Duration) {
        _now = _now.plus(duration)
    }

    /** Rewinds the clock by [duration]. */
    fun rewind(duration: Duration) {
        _now = _now.minus(duration)
    }

    /** Resets the clock back to [initialTime]. */
    fun reset(initialTime: ZonedDateTime = TestFixtures.DEFAULT_NOW) {
        _now = initialTime
    }
}

