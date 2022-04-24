package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.item.SettingsItemsNotifier
import javax.inject.Inject

@AndroidEntryPoint
class SyncFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsItemsNotifier: SettingsItemsNotifier

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