package net.wojteksz128.worktimemeasureapp.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDateTimeProvider @Inject constructor(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    @ApplicationContext private val context: Context,
) : DateTimeProvider(Settings, context) {

    private var _currentTime: ZonedDateTime = ZonedDateTime.now()

    override val currentTime: ZonedDateTime
        get() = _currentTime

    fun setCurrentTime(dateTime: ZonedDateTime) {
        _currentTime = dateTime
    }

    fun advanceTimeBy(duration: Duration) {
        _currentTime = _currentTime.plus(duration)
    }

    override fun updateOffset() {
        // Lock attempt to update offset using NTP server
    }
}