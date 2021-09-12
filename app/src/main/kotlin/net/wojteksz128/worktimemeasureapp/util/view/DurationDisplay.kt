package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.DurationDisplayBinding
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import org.threeten.bp.Duration

class DurationDisplay(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {
    lateinit var binding: DurationDisplayBinding

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.DurationDisplay, 0, 0)

        if (isInEditMode) {
            this.addView(layoutInflater.inflate(R.layout.duration_display, null))
            findViewById<TextView>(R.id.duration_display_title).text = readTitle(typedArray, context)
        } else {
            binding = DurationDisplayBinding.inflate(layoutInflater)
            this.addView(binding.root)

            binding.title = readTitle(typedArray, context)
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
                DateTimeUtils.formatCounterTime(duration, R.string.duration_display_value_template)
        }
    }

    companion object {
        private const val RESOURCE_NOT_FOUND = Int.MIN_VALUE
    }
}