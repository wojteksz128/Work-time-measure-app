package net.wojteksz128.worktimemeasureapp.settings.item

import org.threeten.bp.Duration

open class DurationSettingsItem(name: Int) : SettingsItem<Duration>(
    name,
    { sharedPreferences, key ->
        Duration.ofMinutes(sharedPreferences.getInt(key, 0).toLong())
    })
