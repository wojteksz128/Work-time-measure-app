package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.*
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentDateTimePickerBinding
import net.wojteksz128.worktimemeasureapp.util.view.util.ObservableDelegate
import java.util.*

@BindingMethods(
    BindingMethod(type = DateTimePicker::class, attribute = "time", method = "setTime"),
    BindingMethod(
        type = DateTimePicker::class,
        attribute = "timeAttrChanged",
        method = "setTimeChangeListener"
    )
)
@InverseBindingMethods(
    InverseBindingMethod(type = DateTimePicker::class, attribute = "time", method = "getTime")
)
class DateTimePicker(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private lateinit var binding: ComponentDateTimePickerBinding
    private val model =
        ObservableModel(this::timeChangeListener, DateFormat.is24HourFormat(context))

    init {
        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.component_date_time_picker, this, true)
        } else {
            binding =
                ComponentDateTimePickerBinding.inflate(LayoutInflater.from(context), this, true)
                    .apply {
                        this.model = this@DateTimePicker.model
                    }
        }
    }

    var time: Date?
        get() = model.time
        set(value) {
            model.time = value ?: Date(0)
        }

    var timeChangeListener: InverseBindingListener? = null

    class ObservableModel(
        timeChangeListenerProvider: () -> InverseBindingListener?,
        val is24HourFormat: Boolean
    ) :
        BaseObservable() {
        private var mCalendar = Calendar.getInstance()

        @get:Bindable
        var hour by ObservableDelegate(
            BR.hour,
            if (is24HourFormat) 0 else 12   // 00:00 -> 12:00 AM
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                val fieldType = if (is24HourFormat) Calendar.HOUR_OF_DAY else Calendar.HOUR
                val fieldValue = if (is24HourFormat) newValue % 24
                else newValue % 12

                mCalendar.set(fieldType, fieldValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var minute by ObservableDelegate(BR.minute, 0) { oldValue, newValue ->
            if (oldValue != newValue) {
                mCalendar.set(Calendar.MINUTE, newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var second by ObservableDelegate(BR.second, 0) { oldValue, newValue ->
            if (oldValue != newValue) {
                mCalendar.set(Calendar.SECOND, newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var isAm by ObservableDelegate(BR.am, true) { oldValue, newValue ->
            if (oldValue != newValue) {
                if (newValue) {
                    mCalendar.set(Calendar.AM_PM, Calendar.AM)
                    hour = if (is24HourFormat) mCalendar.get(Calendar.HOUR_OF_DAY)
                    else if (mCalendar.get(Calendar.HOUR) == 0) 12 else mCalendar.get(Calendar.HOUR)
                }
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var isPm by ObservableDelegate(BR.pm, false) { oldValue, newValue ->
            if (oldValue != newValue) {
                if (newValue) {
                    mCalendar.set(Calendar.AM_PM, Calendar.PM)
                    hour = if (is24HourFormat) mCalendar.get(Calendar.HOUR_OF_DAY)
                    else if (mCalendar.get(Calendar.HOUR) == 0) 12 else mCalendar.get(Calendar.HOUR)
                }
                timeChangeListenerProvider()?.onChange()
            }
        }

        fun onAmClick() {
            isAm = true
            isPm = false
        }

        fun onPmClick() {
            isAm = false
            isPm = true
        }

        internal var time: Date
            get() = mCalendar.time
            set(value) {
                mCalendar.time = value
                hour = if (is24HourFormat) mCalendar.get(Calendar.HOUR_OF_DAY)
                else if (mCalendar.get(Calendar.HOUR) == 0) 12 else mCalendar.get(Calendar.HOUR)
                minute = mCalendar.get(Calendar.MINUTE)
                second = mCalendar.get(Calendar.SECOND)
                isAm = mCalendar.get(Calendar.AM_PM) == Calendar.AM
                isPm = mCalendar.get(Calendar.AM_PM) == Calendar.PM
            }
    }
}
