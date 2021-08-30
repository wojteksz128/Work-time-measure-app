package net.wojteksz128.worktimemeasureapp.settings

open class IntFromStringSettingsItem(name: Int) : SettingsItem<Int>(
    name,
    { sharedPreferences, key, defaultValue ->
        sharedPreferences.getString(
            key,
            defaultValue.toString()
        )?.toInt()
    }
)