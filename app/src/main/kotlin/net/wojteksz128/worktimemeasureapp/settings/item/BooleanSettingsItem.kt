package net.wojteksz128.worktimemeasureapp.settings.item

open class BooleanSettingsItem(keyResourceId: Int) : SettingsItem<Boolean>(
    keyResourceId,
    { sharedPreferences, key -> sharedPreferences.getBoolean(key, false) },
    { editor, key, value -> editor.putBoolean(key, value) })
