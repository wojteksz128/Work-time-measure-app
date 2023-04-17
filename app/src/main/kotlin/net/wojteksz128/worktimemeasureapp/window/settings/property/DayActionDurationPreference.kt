package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.window.settings.property.DayActionDurationPreferenceDialog.DayActionDurationPreferenceDialogListener

// This class is used in our preference where user can pick a time for notifications to appear.
// Specifically, this class is responsible for saving/retrieving preference data.
class DayActionDurationPreference(context: Context, attrs: AttributeSet?) :
    DialogPreference(context, attrs), DayActionDurationPreferenceDialogListener {

    private var durationInMinutes: Int
        get() = getPersistedInt(DEFAULT_DURATION_IN_MINUTES)
        set(value) {
            persistInt(value)
            notifyChanged()
        }

    override fun getSummary(): CharSequence {
        val timeString = formatDuration(durationInMinutes)
        return context.let {
            if (timeString.isEmpty()) it.getString(R.string.settings_workTime_week_duration_summary)
            else it.getString(
                R.string.settings_workTime_week_duration_summaryValue,
                timeString,
                it.getString(R.string.settings_workTime_week_duration_summary)
            )
        }
    }

    private fun formatDuration(minutesFromMidnight: Int): String {
        return if (minutesFromMidnight == 0) ""
        else "${minutesFromMidnight / 60}:${if (minutesFromMidnight % 60 < 10) "0" else ""}${minutesFromMidnight % 60}"
    }

    override fun onDurationChangeDialogBinding(dialogBindingMethod: (Int) -> Unit) {
        dialogBindingMethod(durationInMinutes)
    }

    override fun onDurationChangeAccept(durationInMinutes: Int) {
        this.durationInMinutes = durationInMinutes
    }

    // Mostly for default values
    companion object {
        // By default we want notification to appear at 8 AM each time.
        private const val DEFAULT_HOUR = 8
        const val DEFAULT_DURATION_IN_MINUTES = DEFAULT_HOUR * 60
    }
}
