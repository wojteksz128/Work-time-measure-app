package net.wojteksz128.worktimemeasureapp.settings

import org.joda.time.Duration

open class DurationSettingsItem(name: Int) : SettingsItem<Duration>(
    name,
    { sharedPreferences, key ->
        Duration.standardMinutes(sharedPreferences.getInt(key, 0).toLong())
    })
