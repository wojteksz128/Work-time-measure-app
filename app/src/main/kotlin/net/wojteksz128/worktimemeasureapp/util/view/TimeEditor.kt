package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.*
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.BR
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentTimeEditorBinding
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.view.util.ObservableDelegate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
@BindingMethods(
    BindingMethod(type = TimeEditor::class, attribute = "time", method = "setTime"),
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
    )
)
@InverseBindingMethods(
    InverseBindingMethod(type = TimeEditor::class, attribute = "time", method = "getTime"),
    InverseBindingMethod(
        type = TimeEditor::class,
        attribute = "inTimeEditMode",
        method = "getInTimeEditMode"
    )
)
class TimeEditor(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private lateinit var binding: ComponentTimeEditorBinding
    private val model =
        ObservableModel(this::timeChangeListener, this::inTimeEditModeChangeListener)

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

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
                    this.dateTimeUtils = this@TimeEditor.dateTimeUtils
                }
            typedArray.apply {
                model.title = getString(R.styleable.ContainsTitle_title) ?: ""
            }
        }
    }

    var time: Date?
        get() = model.time
        set(value) {
            model.time = value
        }

    var timeChangeListener: InverseBindingListener? = null

    var inTimeEditMode: Boolean
        get() = model.editMode
        set(value) {
            model.editMode = value
        }

    var inTimeEditModeChangeListener: InverseBindingListener? = null

    class ObservableModel(
        timeChangeListenerProvider: () -> InverseBindingListener?,
        inEditModeChangeListenerProvider: () -> InverseBindingListener?
    ) :
        BaseObservable() {
        @get:Bindable
        var title by ObservableDelegate(BR.title, "")

        @get:Bindable
        var time by ObservableDelegate<Date?>(BR.time, null) { oldValue, newValue ->
            if (oldValue != newValue)
                timeChangeListenerProvider()?.onChange()
        }

        @get:Bindable
        var editedTime by ObservableDelegate<Date?>(BR.editedTime, null)

        @get:Bindable
        var editMode by ObservableDelegate(BR.editMode, false) { oldValue, newValue ->
            if (oldValue != newValue) {
                inEditModeChangeListenerProvider()?.onChange()
            }
        }

        fun onSetTimeClick() {
            editedTime = time
            editMode = true
        }

        fun onClearTimeClick() {
            time = null
        }

        fun onAcceptClick() {
            time = editedTime
            editMode = false
        }

        fun onDismissClick() {
            editMode = false
        }
    }
}