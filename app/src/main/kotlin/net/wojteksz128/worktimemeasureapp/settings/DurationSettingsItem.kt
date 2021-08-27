package net.wojteksz128.worktimemeasureapp.settings

import org.joda.time.Duration

open class DurationSettingsItem(name: String) : SettingsItem<Duration>(
    name,
    { sharedPreferences, key, defaultValue ->
        Duration.millis(sharedPreferences.getLong(key, 0)) ?: defaultValue
    })
