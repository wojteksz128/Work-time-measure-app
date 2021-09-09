package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import net.wojteksz128.worktimemeasureapp.settings.Settings

class SettingsItemsNotifier(private val contextGetter: () -> Context?) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Settings.notifyItem(key, contextGetter())
    }
}