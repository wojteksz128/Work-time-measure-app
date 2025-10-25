package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class StringsArraySettingsItem(name: Int, context: Context) : SettingsItem<Set<String>>(
    name,
    context,
    { key -> getStringSet(key, emptySet()) },
    { key, value -> putStringSet(key, value) }
)