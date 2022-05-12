package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class LongSettingsItem(name: Int, context: Context) : SettingsItem<Long>(
    name,
    context,
    { sharedPreferences, key -> sharedPreferences.getLong(key, 0L) },
    { editor, key, value -> editor.putLong(key, value) }
)