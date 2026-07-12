package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentTimeEditorBinding
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.formatToString
import net.wojteksz128.worktimemeasureapp.util.view.util.ObservableDelegate
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

@BindingMethods(
    BindingMethod(type = TimeEditor::class, attribute = "time", method = "setTime"),
    BindingMethod(type = TimeEditor::class, attribute = "workDayDate", method = "setWorkDayDate"),
    BindingMethod(
        type = TimeEditor::class,
        attribute = "timeAttrChanged",
        method = "setTimeChangeListener"
    ),
    BindingMethod(
        type = TimeEditor::class,
        attribute = "inTimeEditMode",
        method = "setInTimeEditMode"
    ),
    BindingMethod(
        type = TimeEditor::class,
        attribute = "inTimeEditModeAttrChanged",
        method = "setInTimeEditModeChangeListener"
    ),
    BindingMethod(
        type = TimeEditor::class,
        attribute = "useFullFormat",
        method = "setUseFullFormat"
    ),
)
@InverseBindingMethods(
    InverseBindingMethod(type = TimeEditor::class, attribute = "time", method = "getTime"),
    InverseBindingMethod(
        type = TimeEditor::class,
        attribute = "workDayDate",
        method = "getWorkDayDate"
    ),
    InverseBindingMethod(
        type = TimeEditor::class,
        attribute = "inTimeEditMode",
        method = "getInTimeEditMode"
    ),
)
class TimeEditor(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private lateinit var binding: ComponentTimeEditorBinding
    private val model =
        ObservableModel(
            context,
            this::timeChangeListener,
            this::inTimeEditModeChangeListener
        )

    init {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ContainsTitle, 0, 0)

        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.component_time_editor, this, true)
            findViewById<TextView>(R.id.time_editor_title).text =
                typedArray.getString(R.styleable.ContainsTitle_title)
        } else {
            binding = ComponentTimeEditorBinding.inflate(LayoutInflater.from(context), this, true)
                .apply {
                    this.model = this@TimeEditor.model
                }
            typedArray.apply {
                model.title = getString(R.styleable.ContainsTitle_title) ?: ""
            }
        }
    }

    var time: LocalDateTime?
        get() = model.time
        set(value) {
            model.time = value
        }

    var workDayDate: LocalDate?
        get() = model.workDayDate
        set(value) {
            model.workDayDate = value
        }

    var timeChangeListener: InverseBindingListener? = null

    var inTimeEditMode: Boolean
        get() = model.editMode
        set(value) {
            model.editMode = value
        }

    var inTimeEditModeChangeListener: InverseBindingListener? = null

    var useFullFormat: Boolean
        get() = model.useFullFormat
        set(value) {
            model.useFullFormat = value
        }

    class ObservableModel(
        private val context: Context,
        timeChangeListenerProvider: () -> InverseBindingListener?,
        inEditModeChangeListenerProvider: () -> InverseBindingListener?,
    ) : BaseObservable(), ClassTagAware {
        @get:Bindable
        var title by ObservableDelegate(BR.title, "")

        @get:Bindable
        var time by ObservableDelegate<LocalDateTime?>(BR.time, null) { oldValue, newValue ->
            if (oldValue != newValue)
                notifyPropertyChanged(BR.formattedTime)
                timeChangeListenerProvider()?.onChange()
        }

        @get:Bindable
        var editedTime by ObservableDelegate<LocalDateTime?>(BR.editedTime, null)

        @get:Bindable
        var workDayDate by ObservableDelegate<LocalDate?>(BR.workDayDate, null)
        { oldValue, newValue ->
            if (oldValue != newValue)
                timeChangeListenerProvider()?.onChange()
        }

        @get:Bindable
        var editMode by ObservableDelegate(BR.editMode, false) { oldValue, newValue ->
            if (oldValue != newValue) {
                inEditModeChangeListenerProvider()?.onChange()
            }
        }

        @get:Bindable
        var useFullFormat by ObservableDelegate(BR.useFullFormat, false)

        @get:Bindable
        val formattedTime: String
            get() {
                val currentTime =
                    time ?: return context.getString(R.string.time_editor_value_not_set)
                val formatPattern = context.getString(
                    if (useFullFormat) R.string.time_editor_value_long_format
                    else R.string.time_editor_value_short_format
                )
                return currentTime.formatToString(formatPattern)
            }

        fun onSetTimeClick() {
            editedTime = time?.let { LocalDateTime.from(it) }
            editMode = true
        }

        fun onClearTimeClick() {
            time = null
        }

        fun onAcceptClick() {
            Log.d(classTag, "onAcceptClick: time: $time, editedTime: $editedTime")
            time = editedTime
            editMode = false
        }

        fun onDismissClick() {
            editMode = false
        }
    }
}