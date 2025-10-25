package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class IntFromStringSettingsItem(name: Int, context: Context) : SettingsItem<Int>(
    name,
    context,
    { key -> getString(key, null)?.toInt() },
    { key, value -> putString(key, value.toString()) })