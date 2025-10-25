package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import java.net.InetAddress

open class InetAddressSettingsItem(name: Int, context: Context) : SettingsItem<InetAddress?>(
    name,
    context,
    { key -> getString(key, null)?.let { InetAddress.getByName(it) } },
    { key, value -> putString(key, value?.hostAddress) })