package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import androidx.annotation.StringRes

class EnumSettingsItem<T : Enum<T>>(@StringRes name: Int, context: Context, enumValues: Array<T>) :
    SettingsItem<T>(
        name,
        context,
        { key -> enumValues.firstOrNull { it.name == getString(key, null) } },
        { key, value -> putString(key, value.name) })
