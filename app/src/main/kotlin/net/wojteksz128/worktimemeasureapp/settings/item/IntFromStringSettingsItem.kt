package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

open class IntFromStringSettingsItem(name: Int, context: Context) : SettingsItem<Int>(
    name,
    context,
    { sharedPreferences, key ->
        sharedPreferences.getString(
            key,
            null
        )?.toInt()
    }, { editor, key, value ->
        editor.putString(key, value.toString())
    }
)