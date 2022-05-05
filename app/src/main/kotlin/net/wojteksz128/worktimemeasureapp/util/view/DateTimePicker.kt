package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.*
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentDateTimePickerBinding
import net.wojteksz128.worktimemeasureapp.util.view.util.ObservableDelegate
import java.util.*

class DateTimePicker(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private lateinit var binding: ComponentDateTimePickerBinding
    private val model = ObservableModel()

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

    companion object {
        @InverseBindingAdapter(attribute = "time")
        @JvmStatic
        fun getTime(obj: DateTimePicker) = obj.model.time

        @BindingAdapter("time")
        @JvmStatic
        fun setTime(obj: DateTimePicker, time: Date?) {
            val insertedTime = time ?: Date(0)
            if (insertedTime != obj.model.time)
                obj.model.time = insertedTime
        }

        @BindingAdapter("app:timeAttrChanged")
        @JvmStatic
        fun setListeners(obj: DateTimePicker, attrChange: InverseBindingListener) {
            val model = obj.model
            obj.binding.apply {
                dateTimePickerHour.setOnValueChangedListener { _, _, newValue ->
                    model.hour = newValue
                    attrChange.onChange()
                }
                dateTimePickerMinute.setOnValueChangedListener { _, _, newValue ->
                    model.minute = newValue
                    attrChange.onChange()
                }
                dateTimePickerSecond.setOnValueChangedListener { _, _, newValue ->
                    model.second = newValue
                    attrChange.onChange()
                }
                dateTimePickerAm.setOnCheckedChangeListener { _, isChecked ->
                    model.isAm = isChecked
                    model.isPm = !isChecked
                    if (isChecked)
                        attrChange.onChange()
                }
                dateTimePickerPm.setOnCheckedChangeListener { _, isChecked ->
                    model.isAm = !isChecked
                    model.isPm = isChecked
                    if (isChecked)
                        attrChange.onChange()
                }
            }
        }
    }

    class ObservableModel : BaseObservable() {
        private var mCalendar = Calendar.getInstance()

        @get:Bindable
        var hour by ObservableDelegate(BR.hour, 0) {
            mCalendar.set(Calendar.HOUR_OF_DAY, it)
        }

        @get:Bindable
        var minute by ObservableDelegate(BR.minute, 0) {
            mCalendar.set(Calendar.MINUTE, it)
        }

        @get:Bindable
        var second by ObservableDelegate(BR.second, 0) {
            mCalendar.set(Calendar.SECOND, it)
        }

        @get:Bindable
        var isAm by ObservableDelegate(BR.am, true) {
            if (it) {
                mCalendar.set(Calendar.AM_PM, Calendar.AM)
                hour = mCalendar.get(Calendar.HOUR_OF_DAY)
            }
        }

        @get:Bindable
        var isPm by ObservableDelegate(BR.pm, false) {
            if (it) {
                mCalendar.set(Calendar.AM_PM, Calendar.PM)
                hour = mCalendar.get(Calendar.HOUR_OF_DAY)
            }
        }

        internal var time: Date
            get() = mCalendar.time
            set(value) {
                mCalendar.time = value
                hour = mCalendar.get(Calendar.HOUR_OF_DAY)
                minute = mCalendar.get(Calendar.MINUTE)
                second = mCalendar.get(Calendar.SECOND)
                isAm = mCalendar.get(Calendar.AM_PM) == Calendar.AM
                isPm = mCalendar.get(Calendar.AM_PM) == Calendar.PM
            }
    }
}
