package net.wojteksz128.worktimemeasureapp.settings.item

open class IntFromStringSettingsItem(name: Int) : SettingsItem<Int>(
    name,
    { sharedPreferences, key ->
        sharedPreferences.getString(
            key,
            null
        )?.toInt()
    }
)