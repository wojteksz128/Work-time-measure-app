package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentDurationDisplayBinding
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import org.threeten.bp.Duration
import javax.inject.Inject

@AndroidEntryPoint
class DurationDisplay(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {
    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    lateinit var binding: ComponentDurationDisplayBinding

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.DurationDisplay, 0, 0)

        if (isInEditMode) {
            this.addView(layoutInflater.inflate(R.layout.component_duration_display, null))
            findViewById<TextView>(R.id.duration_display_title).text = readTitle(typedArray, context)
        } else {
            binding = ComponentDurationDisplayBinding.inflate(layoutInflater)
            this.addView(binding.root)

            binding.title = readTitle(typedArray, context)
            binding.dateTimeUtils = dateTimeUtils
        }
    }

    private fun readTitle(typedArray: TypedArray, context: Context): String? {
        val titleResId =
            typedArray.getResourceId(R.styleable.DurationDisplay_title, RESOURCE_NOT_FOUND)
        return if (titleResId == RESOURCE_NOT_FOUND)
            typedArray.getString(R.styleable.DurationDisplay_title)
        else context.getString(titleResId)
    }

    fun setDuration(duration: Duration?) {
        if (!isInEditMode)
            binding.duration = duration
        else {
            findViewById<TextView>(R.id.duration_display_value).text =
                dateTimeUtils.formatCounterTime(duration, R.string.duration_display_value_template)
        }
    }

    companion object {
        private const val RESOURCE_NOT_FOUND = Int.MIN_VALUE
    }
}