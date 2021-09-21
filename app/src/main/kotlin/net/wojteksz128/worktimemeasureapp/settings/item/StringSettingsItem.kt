package net.wojteksz128.worktimemeasureapp.settings.item

open class StringSettingsItem(name: Int) : SettingsItem<String>(
    name,
    { sharedPreferences, key -> sharedPreferences.getString(key, null) },
    { editor, key, value -> editor.putString(key, value) }
)