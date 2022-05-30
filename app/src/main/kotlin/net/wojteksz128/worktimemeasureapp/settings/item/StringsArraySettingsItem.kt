package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class StringsArraySettingsItem(name: Int, context: Context) : SettingsItem<Set<String>>(
    name,
    context,
    { sharedPreferences, key -> sharedPreferences.getStringSet(key, emptySet()) },
    { editor, key, value -> editor.putStringSet(key, value) }
)