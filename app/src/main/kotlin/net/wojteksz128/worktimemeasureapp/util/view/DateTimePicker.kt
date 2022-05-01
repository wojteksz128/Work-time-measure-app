package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
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

    var time: Date?
        get() = model.time
        set(value) {
            model.time = value ?: Date(0)
        }

    class ObservableModel : BaseObservable() {
        private var mCalendar = Calendar.getInstance()

        @get:Bindable
        var hour by ObservableDelegate(BR.hour, 0)

        @get:Bindable
        var minute by ObservableDelegate(BR.minute, 0)

        @get:Bindable
        var second by ObservableDelegate(BR.second, 0)

        @get:Bindable
        var isAm by ObservableDelegate(BR.am, true)

        @get:Bindable
        var isPm by ObservableDelegate(BR.pm, false)

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

        fun onAmClick() {
            isAm = true
            isPm = false
        }

        fun onPmClick() {
            isAm = false
            isPm = true
        }
    }
}
