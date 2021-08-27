package net.wojteksz128.worktimemeasureapp.settings

open class StringSettingsItem(name: Int) : SettingsItem<String>(
    name,
    { sharedPreferences, key, defaultValue -> sharedPreferences.getString(key, defaultValue) })