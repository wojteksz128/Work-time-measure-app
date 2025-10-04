package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentDateTimePickerBinding
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.toLocalDate
import net.wojteksz128.worktimemeasureapp.util.view.util.ObservableDelegate
import org.threeten.bp.LocalDate
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import java.util.Calendar
import java.util.Date

@BindingMethods(
    BindingMethod(type = DateTimePicker::class, attribute = "time", method = "setTime"),
    BindingMethod(
        type = DateTimePicker::class,
        attribute = "workDayDate",
        method = "setWorkDayDate"
    ),
    BindingMethod(
        type = DateTimePicker::class,
        attribute = "timeAttrChanged",
        method = "setTimeChangeListener"
    )
)
@InverseBindingMethods(
    InverseBindingMethod(type = DateTimePicker::class, attribute = "time", method = "getTime"),
    InverseBindingMethod(
        type = DateTimePicker::class,
        attribute = "workDayDate",
        method = "getWorkDayDate"
    )
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

    var workDayDate: LocalDate?
        get() = model.workDayDate
        set(value) {
            model.workDayDate = value ?: LocalDate.now()
        }

    var timeChangeListener: InverseBindingListener? = null

    class ObservableModel(
        timeChangeListenerProvider: () -> InverseBindingListener?,
        val is24HourFormat: Boolean,
    ) :
        BaseObservable(), ClassTagAware {
        private var mCalendar = Calendar.getInstance()
        private var mWorkDayDate: LocalDate? = LocalDate.now()

        internal var workDayDate: LocalDate?
            get() = mWorkDayDate
            set(value) {
                mWorkDayDate = value
                Log.d(
                    classTag,
                    "workDayDate: value: $value, time.toLocalDate(): ${time.toLocalDate()}, old isSelectDate: $isSelectDate, new isSelectDate: ${value == time.toLocalDate()}"
                )
                isSelectDate = value == time.toLocalDate()
            }

        @get:Bindable
        var year by ObservableDelegate(
            BR.year,
            mCalendar.get(Calendar.YEAR)
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setYear: old: $oldValue, new: $newValue")
                mCalendar.set(Calendar.YEAR, newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var month by ObservableDelegate(
            BR.month,
            mCalendar.get(Calendar.MONTH) + 1
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setMonth: old: $oldValue, new: $newValue")
                mCalendar.set(Calendar.MONTH, newValue - 1)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var day by ObservableDelegate(
            BR.day,
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setDay: old: $oldValue, new: $newValue")
                mCalendar.set(Calendar.DAY_OF_MONTH, newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var hour by ObservableDelegate(
            BR.hour,
            if (is24HourFormat) 0 else 12   // 00:00 -> 12:00 AM
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setHour: old: $oldValue, new: $newValue")
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
                Log.d(classTag, "setMinute: old: $oldValue, new: $newValue")
                mCalendar.set(Calendar.MINUTE, newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var second by ObservableDelegate(BR.second, 0) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setSecond: old: $oldValue, new: $newValue")
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

        @get:Bindable
        var isSelectDate by ObservableDelegate(
            BR.selectDate,
            false
        )

        fun getMinYear(): Int = 1970

        fun getCurrentYear(): Int = Year.now().value

        fun getMaxMonthDay(): Int = YearMonth.of(year, month).lengthOfMonth()

        fun onAmClick() {
            isAm = true
            isPm = false
        }

        fun onPmClick() {
            isAm = false
            isPm = true
        }

        fun onSelectDateClick() {
            isSelectDate = !isSelectDate
            if (isSelectDate) {
                val currentDate = LocalDate.now()
                year = workDayDate?.year ?: currentDate.year
                month = workDayDate?.monthValue ?: currentDate.month.value
                day = workDayDate?.dayOfMonth ?: currentDate.dayOfMonth
            }
        }

        internal var time: Date
            get() = mCalendar.time
            set(value) {
                mCalendar.time = value
                year = mCalendar.get(Calendar.YEAR)
                month = mCalendar.get(Calendar.MONTH) + 1
                day = mCalendar.get(Calendar.DAY_OF_MONTH)
                hour = if (is24HourFormat) mCalendar.get(Calendar.HOUR_OF_DAY)
                else if (mCalendar.get(Calendar.HOUR) == 0) 12 else mCalendar.get(Calendar.HOUR)
                minute = mCalendar.get(Calendar.MINUTE)
                second = mCalendar.get(Calendar.SECOND)
                isAm = mCalendar.get(Calendar.AM_PM) == Calendar.AM
                isPm = mCalendar.get(Calendar.AM_PM) == Calendar.PM
                Log.d(
                    classTag,
                    "setTime: workDayDate: $workDayDate, time.toLocalDate(): ${time.toLocalDate()}, old isSelectDate: $isSelectDate, new isSelectDate: ${workDayDate == time.toLocalDate()}"
                )
                isSelectDate = workDayDate == time.toLocalDate()
            }
    }
}
