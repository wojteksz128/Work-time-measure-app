package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import java.net.InetAddress

open class InetAddressSettingsItem(name: Int, context: Context) : SettingsItem<InetAddress?>(
    name,
    context,
    { sharedPreferences, key ->
        sharedPreferences.getString(key, null)
            ?.let { InetAddress.getByName(it) }
    },
    { editor, key, value -> editor.putString(key, value?.hostAddress).apply() }
)