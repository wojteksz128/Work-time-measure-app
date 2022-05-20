package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import net.wojteksz128.worktimemeasureapp.R

// This class is used in our preference where user can pick a time for notifications to appear.
// Specifically, this class is responsible for saving/retrieving preference data.
class TimePickerPreference(context: Context?, attrs: AttributeSet?) :
    DialogPreference(context, attrs) {

    // Get saved preference value (in minutes from midnight, so 1 AM is represented as 1*60 here
    fun getPersistedMinutesFromMidnight(): Int {
        return super.getPersistedInt(DEFAULT_MINUTES_FROM_MIDNIGHT)
    }

    // Save preference
    fun persistMinutesFromMidnight(minutesFromMidnight: Int) {
        super.persistInt(minutesFromMidnight)
        notifyChanged()
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)
        val timeString = minutesFromMidnightToHourlyTime(getPersistedMinutesFromMidnight())
        context?.let {
            summary =
                if (timeString.isEmpty()) it.getString(R.string.settings_workTime_week_duration_summary)
                else it.getString(
                    R.string.settings_workTime_week_duration_summaryValue,
                    timeString,
                    it.getString(R.string.settings_workTime_week_duration_summary)
                )
        }
    }

    // Mostly for default values
    companion object {
        // By default we want notification to appear at 8 AM each time.
        private const val DEFAULT_HOUR = 8
        const val DEFAULT_MINUTES_FROM_MIDNIGHT = DEFAULT_HOUR * 60
    }

}

fun minutesFromMidnightToHourlyTime(minutesFromMidnight: Int): String {
    return if (minutesFromMidnight == 0) ""
    else "${minutesFromMidnight / 60}:${if (minutesFromMidnight % 60 < 10) "0" else ""}${minutesFromMidnight % 60}"
}
