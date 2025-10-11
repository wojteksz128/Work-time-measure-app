package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import net.wojteksz128.worktimemeasureapp.util.android.before
import net.wojteksz128.worktimemeasureapp.util.android.fromVersion

class DayActionDurationPreferenceDialog private constructor(private val listener: DayActionDurationPreferenceDialogListener) :
    PreferenceDialogFragmentCompat() {

    @Suppress("DEPRECATION")
    private var durationInMinutes: Int
        get() = fromVersion(Build.VERSION_CODES.M) {
            (timepicker.hour * 60) + timepicker.minute
        } before {
            (timepicker.currentHour * 60) + timepicker.currentMinute
        }
        set(value) {
            fromVersion(Build.VERSION_CODES.M) {
                timepicker.hour = value / 60
                timepicker.minute = value % 60
            } before {
                timepicker.currentHour = value / 60
                timepicker.currentMinute = value % 60
            }
        }

    private lateinit var timepicker: TimePicker

    override fun onCreateDialogView(context: Context): View {
        timepicker = TimePicker(context).apply {
            @Suppress("UsePropertyAccessSyntax")
            setIs24HourView(true)
        }
        return timepicker
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        listener.onDurationChangeDialogBinding(this::durationInMinutes::set)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        // Save settings
        if (positiveResult) {
            listener.onDurationChangeAccept(this.durationInMinutes)
        }
    }

    interface DayActionDurationPreferenceDialogListener {
        fun onDurationChangeDialogBinding(dialogBindingMethod: (Int) -> Unit)
        fun onDurationChangeAccept(durationInMinutes: Int)
    }

    companion object {
        fun newInstance(
            key: String,
            listener: DayActionDurationPreferenceDialogListener
        ): DayActionDurationPreferenceDialog {
            val fragment = DayActionDurationPreferenceDialog(listener)
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}