package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.item.SettingsItemsNotifier

class SyncFragment : PreferenceFragmentCompat() {

    private val settingsItemsNotifier = SettingsItemsNotifier { context }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sync_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(
            settingsItemsNotifier
        )
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            settingsItemsNotifier
        )
    }
}