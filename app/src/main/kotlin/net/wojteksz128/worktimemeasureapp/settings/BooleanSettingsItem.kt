package net.wojteksz128.worktimemeasureapp.settings

open class BooleanSettingsItem(keyResourceId: Int) : SettingsItem<Boolean>(
    keyResourceId,
    { sharedPreferences, key ->
        sharedPreferences.getBoolean(
            key,
            false
        )
    })
