package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class StringSettingsItem(name: Int, context: Context) : SettingsItem<String>(
    name,
    context,
    { key -> getString(key, null) },
    { key, value -> putString(key, value) },
    fromString = { it },
)