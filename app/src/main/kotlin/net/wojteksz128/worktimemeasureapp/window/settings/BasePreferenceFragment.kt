package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.settings.item.SettingsItemsNotifier
import javax.inject.Inject

@AndroidEntryPoint
abstract class BasePreferenceFragment(
    @XmlRes private val preferenceResId: Int
) : PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsItemsNotifier: SettingsItemsNotifier

    final override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(preferenceResId, rootKey)
        onPreferencesInit()
    }

    open fun onPreferencesInit() {}

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(
            settingsItemsNotifier
        )
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(
            settingsItemsNotifier
        )
    }
}