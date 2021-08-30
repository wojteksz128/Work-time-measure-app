package net.wojteksz128.worktimemeasureapp.settings

open class BooleanSettingsItem(keyResourceId: Int) : SettingsItem<Boolean>(
    keyResourceId,
    { sharedPreferences, key, defaultValue ->
        sharedPreferences.getBoolean(
            key,
            defaultValue ?: false
        )
    })
