package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import java.net.InetAddress

open class InetAddressSettingsItem(name: Int, context: Context) :
    AsyncSettingsItem<InetAddress, String>(
    name,
    context,
        { hostname -> InetAddress.getByName(hostname) },
        { key -> getString(key, null) },
        { key, value -> putString(key, value.hostName) })