package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class LongSettingsItem(name: Int, context: Context) : SettingsItem<Long>(
    name,
    context,
    { key -> getLong(key, 0L) },
    { key, value -> putLong(key, value) }
)