package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class BooleanSettingsItem(
    keyResourceId: Int,
    context: Context,
    defaultValue: Boolean = false,
) : SettingsItem<Boolean>(
    keyResourceId,
    context,
    { sharedPreferences, key -> sharedPreferences.getBoolean(key, defaultValue) },
    { editor, key, value -> editor.putBoolean(key, value) })
