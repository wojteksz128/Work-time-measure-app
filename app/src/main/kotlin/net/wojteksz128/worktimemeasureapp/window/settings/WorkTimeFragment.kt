package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.item.SettingsItemsNotifier
import net.wojteksz128.worktimemeasureapp.window.settings.property.TimePickerPreference
import net.wojteksz128.worktimemeasureapp.window.settings.property.TimePickerPreferenceDialog
import javax.inject.Inject

class WorkTimeFragment @Inject constructor(
    private val settingsItemsNotifier: SettingsItemsNotifier
) : PreferenceFragmentCompat() {

    private val DIALOG_FRAGMENT_TAG = "TimePickerDialog"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.work_time_preferences, rootKey)
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

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is TimePickerPreference) {
            val timePickerDialog = TimePickerPreferenceDialog.newInstance(preference.key)
            timePickerDialog.setTargetFragment(this, 0)
            timePickerDialog.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}