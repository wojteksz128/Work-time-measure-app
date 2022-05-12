package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class StringSettingsItem(name: Int, context: Context) : SettingsItem<String>(
    name,
    context,
    { sharedPreferences, key -> sharedPreferences.getString(key, null) },
    { editor, key, value -> editor.putString(key, value) }
)