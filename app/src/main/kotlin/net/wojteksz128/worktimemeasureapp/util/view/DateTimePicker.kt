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
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.view.util.ObservableDelegate
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

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
        ObservableModel(
            this::timeChangeListener,
            dateTimeProvider.currentTimeZone,
            DateFormat.is24HourFormat(context)
        )

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

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

    var time: LocalDateTime?
        get() = model.time
        set(value) {
            model.time = value ?: LocalDateTime.now(dateTimeProvider.currentTimeZone)
        }

    var workDayDate: LocalDate?
        get() = model.workDayDate
        set(value) {
            model.workDayDate = value ?: LocalDate.now(dateTimeProvider.currentTimeZone)
        }

    var timeChangeListener: InverseBindingListener? = null

    class ObservableModel(
        timeChangeListenerProvider: () -> InverseBindingListener?,
        private val timeZone: ZoneId,
        val is24HourFormat: Boolean,
    ) :
        BaseObservable(), ClassTagAware {
        private var mDateTime = LocalDateTime.now(timeZone).truncatedTo(ChronoUnit.SECONDS)
        private var mWorkDayDate: LocalDate? = LocalDate.now(timeZone)

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
            mDateTime.year
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setYear: old: $oldValue, new: $newValue")
                mDateTime = mDateTime.withYear(newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var month by ObservableDelegate(
            BR.month,
            mDateTime.month.value
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setMonth: old: $oldValue, new: $newValue")
                mDateTime = mDateTime.withMonth(newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var day by ObservableDelegate(
            BR.day,
            mDateTime.dayOfMonth
        ) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setDay: old: $oldValue, new: $newValue")
                mDateTime = mDateTime.withDayOfMonth(newValue)
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
                val newValue24 = if (is24HourFormat) newValue % 24
                else newValue % 12 + if (isAm) 0 else 12

                mDateTime = mDateTime.withHour(newValue24)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var minute by ObservableDelegate(BR.minute, 0) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setMinute: old: $oldValue, new: $newValue")
                mDateTime = mDateTime.withMinute(newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var second by ObservableDelegate(BR.second, 0) { oldValue, newValue ->
            if (oldValue != newValue) {
                Log.d(classTag, "setSecond: old: $oldValue, new: $newValue")
                mDateTime = mDateTime.withSecond(newValue)
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var isAm by ObservableDelegate(BR.am, true) { oldValue, newValue ->
            if (oldValue != newValue) {
                if (newValue) {
                    val hour = mDateTime.hour
                    mDateTime = mDateTime.withHour(
                        if (is24HourFormat) hour else when (hour) {
                            in 0..11 -> hour
                            else -> hour % 12
                        }
                    )
                }
                timeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var isPm by ObservableDelegate(BR.pm, false) { oldValue, newValue ->
            if (oldValue != newValue) {
                if (newValue) {
                    val hour = mDateTime.hour
                    mDateTime = mDateTime.withHour(
                        if (is24HourFormat) hour else when (hour) {
                            in 0..11 -> hour + 12
                            else -> hour
                        }
                    )
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

        fun getCurrentYear(): Int = Year.now(timeZone).value

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
                val currentDate = LocalDate.now(timeZone)
                year = workDayDate?.year ?: currentDate.year
                month = workDayDate?.monthValue ?: currentDate.month.value
                day = workDayDate?.dayOfMonth ?: currentDate.dayOfMonth
            }
        }

        internal var time: LocalDateTime
            get() = mDateTime
            set(value) {
                year = value.year
                month = value.month.value
                day = value.dayOfMonth
                minute = value.minute
                second = value.second
                mDateTime.hour.let {
                    isAm = it < 12
                    isPm = it >= 12
                    hour = if (is24HourFormat) {
                        it
                    } else {
                        when (it) {
                            0 -> 12
                            in 1..12 -> it
                            else -> it % 12
                        }
                    }
                }
                Log.d(
                    classTag,
                    "setTime: workDayDate: $workDayDate, time.toLocalDate(): ${time.toLocalDate()}, old isSelectDate: $isSelectDate, new isSelectDate: ${workDayDate == time.toLocalDate()}"
                )
                isSelectDate = workDayDate == time.toLocalDate()
            }
    }
}
