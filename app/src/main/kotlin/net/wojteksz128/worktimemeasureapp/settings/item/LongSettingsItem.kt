package net.wojteksz128.worktimemeasureapp.settings.item

open class LongSettingsItem(name: Int) : SettingsItem<Long>(
    name,
    { sharedPreferences, key -> sharedPreferences.getLong(key, 0L) },
    { editor, key, value -> editor.putLong(key, value) }
)