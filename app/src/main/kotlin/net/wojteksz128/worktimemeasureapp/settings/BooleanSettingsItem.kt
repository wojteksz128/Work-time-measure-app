package net.wojteksz128.worktimemeasureapp.settings

open class BooleanSettingsItem(name: String) : SettingsItem<Boolean>(
    name,
    { sharedPreferences, key, defaultValue ->
        sharedPreferences.getBoolean(
            key,
            defaultValue ?: false
        )
    })
