package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.window.settings.property.DayActionDurationPreference
import net.wojteksz128.worktimemeasureapp.window.settings.property.DayActionDurationPreferenceDialog

@AndroidEntryPoint
class WeekWorkTimeFragment : BasePreferenceFragment(R.xml.week_work_time_preferences) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val daysOfWorkingWeekSelector =
            findPreference<MultiSelectListPreference>(getString(R.string.settings_key_workTime_week_daysOfWorkingWeek))
        daysOfWorkingWeekSelector?.let { multiSelectListPreference ->
            multiSelectListPreference.summaryProvider =
                Preference.SummaryProvider { preference: MultiSelectListPreference ->
                    preference.values.map { preference.findIndexOfValue(it) }.sorted()
                        .joinToString(", ") { preference.entries[it] }
                }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is DayActionDurationPreference) {
            val timePickerDialog =
                DayActionDurationPreferenceDialog.newInstance(preference.key, preference)
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