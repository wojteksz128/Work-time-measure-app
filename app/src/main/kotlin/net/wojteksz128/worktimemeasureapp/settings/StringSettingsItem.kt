package net.wojteksz128.worktimemeasureapp.settings

open class StringSettingsItem(name: String) : SettingsItem<String>(
    name,
    { sharedPreferences, key, defaultValue -> sharedPreferences.getString(key, defaultValue) })