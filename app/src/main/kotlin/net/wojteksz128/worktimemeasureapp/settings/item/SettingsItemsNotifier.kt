package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import net.wojteksz128.worktimemeasureapp.settings.Settings

class SettingsItemsNotifier(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    private val context: Context,
) : SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Settings.notifyItemChanged(key, context)
    }
}