package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import androidx.annotation.StringRes

class EnumSettingsItem<T : Enum<T>>(@StringRes name: Int, context: Context, enumValues: Array<T>) :
    SettingsItem<T>(
        name,
        context,
        { sharedPreferences, key ->
            enumValues.firstOrNull {
                it.name == sharedPreferences.getString(key,
                    null)
            }
        },
        { editor, key, value -> editor.putString(key, value.name) }
    )
