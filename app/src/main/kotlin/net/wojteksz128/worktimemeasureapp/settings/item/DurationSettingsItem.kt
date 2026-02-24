package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import org.threeten.bp.Duration

open class DurationSettingsItem(name: Int, context: Context) : SettingsItem<Duration>(
    name,
    context,
    { key -> Duration.ofMinutes(getInt(key, 0).toLong()) },
    { key, duration -> putInt(key, duration.toMinutes().toInt()) },
    fromString = { Duration.ofMinutes(it.toLongOrNull() ?: 0) },
)
