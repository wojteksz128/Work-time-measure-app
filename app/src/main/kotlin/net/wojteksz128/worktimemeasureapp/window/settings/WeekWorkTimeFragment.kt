package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.window.settings.property.TimePickerPreference
import net.wojteksz128.worktimemeasureapp.window.settings.property.TimePickerPreferenceDialog

@AndroidEntryPoint
class WeekWorkTimeFragment : BasePreferenceFragment(R.xml.week_work_time_preferences) {

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is TimePickerPreference) {
            val timePickerDialog = TimePickerPreferenceDialog.newInstance(preference.key)
            // TODO: Popraw na wersję nie "deprecated"
            timePickerDialog.setTargetFragment(this, 0)
            timePickerDialog.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "TimePickerDialog"
    }
}