package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class BooleanSettingsItem(
    keyResourceId: Int,
    context: Context,
    defaultValue: Boolean = false,
) : SettingsItem<Boolean>(
    keyResourceId,
    context,
    { key -> getBoolean(key, defaultValue) },
    { key, value -> putBoolean(key, value) },
    fromString = { it.toBooleanStrictOrNull() ?: defaultValue },
)
