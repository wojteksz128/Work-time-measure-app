package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import net.wojteksz128.worktimemeasureapp.R

class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat() {

    private lateinit var timepicker: TimePicker

    override fun onCreateDialogView(context: Context?): View {
        timepicker = TimePicker(context)
        return timepicker
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        val minutesAfterMidnight = (preference as TimePickerPreference)
            .getPersistedMinutesFromMidnight()
        timepicker.setIs24HourView(true)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timepicker.hour = minutesAfterMidnight / 60
            timepicker.minute = minutesAfterMidnight % 60
        } else {
            timepicker.currentHour = minutesAfterMidnight / 60
            timepicker.currentMinute = minutesAfterMidnight % 60
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        // Save settings
        @Suppress("DEPRECATION")
        if (positiveResult) {
            val minutesAfterMidnight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (timepicker.hour * 60) + timepicker.minute
            } else {
                (timepicker.currentHour * 60) + timepicker.currentMinute
            }
            (preference as TimePickerPreference).persistMinutesFromMidnight(minutesAfterMidnight)
            val timeString =
                minutesFromMidnightToHourlyTime(minutesAfterMidnight)
            context?.let {
                preference.summary =
                    if (timeString.isEmpty()) it.getString(R.string.settings_workTime_week_duration_summary)
                    else it.getString(
                        R.string.settings_workTime_week_duration_summaryValue,
                        timeString,
                        it.getString(R.string.settings_workTime_week_duration_summary)
                    )
            }

        }
    }

    companion object {
        fun newInstance(key: String): TimePickerPreferenceDialog {
            val fragment = TimePickerPreferenceDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}