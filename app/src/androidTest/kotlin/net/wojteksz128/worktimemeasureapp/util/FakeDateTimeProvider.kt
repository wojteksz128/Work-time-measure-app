package net.wojteksz128.worktimemeasureapp.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.fixtures.TestFixtures
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controllable [DateTimeProvider] for instrumented (Android) tests.
 *
 * Integrates with the Hilt DI graph as a `@Singleton`. Inject it wherever
 * [DateTimeProvider] is needed and control time via [setTime], [advance] and [reset].
 *
 * For JVM unit tests (no Hilt), use
 * [net.wojteksz128.worktimemeasureapp.util.fixtures.TestClock] instead.
 */
@Singleton
class FakeDateTimeProvider @Inject constructor(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    @param:ApplicationContext private val context: Context,
) : DateTimeProvider(Settings, context) {

    private var _currentTime: ZonedDateTime = TestFixtures.DEFAULT_NOW

    override val currentTime: ZonedDateTime
        get() = _currentTime

    // ── time control ──────────────────────────────────────────────────────────

    /** Sets the clock to an exact [dateTime]. */
    fun setTime(dateTime: ZonedDateTime) {
        _currentTime = dateTime
    }

    /** Sets the clock to the start of the given [date] (00:00 in the current zone). */
    fun setTime(date: LocalDate) {
        _currentTime = date.atStartOfDay(_currentTime.zone)
    }

    /** Advances the clock forward by [duration]. */
    fun advance(duration: Duration) {
        _currentTime = _currentTime.plus(duration)
    }

    /** Rewinds the clock backward by [duration]. */
    fun rewind(duration: Duration) {
        _currentTime = _currentTime.minus(duration)
    }

    /** Resets the clock to [TestFixtures.DEFAULT_NOW] (or a custom [initialTime]). */
    fun reset(initialTime: ZonedDateTime = TestFixtures.DEFAULT_NOW) {
        _currentTime = initialTime
    }

    // ── legacy alias ──────────────────────────────────────────────────────────

    /** @deprecated Use [setTime] instead. */
    fun setCurrentTime(dateTime: ZonedDateTime) = setTime(dateTime)

    /** @deprecated Use [advance] instead. */
    fun advanceTimeBy(duration: Duration) = advance(duration)

    // ── no-op overrides ───────────────────────────────────────────────────────

    override fun updateOffset() {
        // Intentionally empty — prevents NTP calls during tests.
    }
}