package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class BooleanSettingsItem(keyResourceId: Int, context: Context) : SettingsItem<Boolean>(
    keyResourceId,
    context,
    { sharedPreferences, key -> sharedPreferences.getBoolean(key, false) },
    { editor, key, value -> editor.putBoolean(key, value) })
