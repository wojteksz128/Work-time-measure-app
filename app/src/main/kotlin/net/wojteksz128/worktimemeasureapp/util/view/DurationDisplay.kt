package net.wojteksz128.worktimemeasureapp.util.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ComponentDurationDisplayBinding
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import org.threeten.bp.Duration

class DurationDisplay(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    lateinit var binding: ComponentDurationDisplayBinding

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ContainsTitle, 0, 0)

        if (isInEditMode) {
            this.addView(layoutInflater.inflate(R.layout.component_duration_display, null))
            findViewById<TextView>(R.id.duration_display_title).text = readTitle(typedArray, context)
        } else {
            binding = ComponentDurationDisplayBinding.inflate(layoutInflater)
            this.addView(binding.root)

            binding.title = readTitle(typedArray, context)
        }
    }

    private fun readTitle(typedArray: TypedArray, context: Context): String? {
        val titleResId =
            typedArray.getResourceId(R.styleable.ContainsTitle_title, RESOURCE_NOT_FOUND)
        return if (titleResId == RESOURCE_NOT_FOUND)
            typedArray.getString(R.styleable.ContainsTitle_title)
        else context.getString(titleResId)
    }

    fun setDuration(duration: Duration?) {
        val durationText = duration.toCounterString()

        if (!isInEditMode)
            binding.durationText = durationText
        else {
            findViewById<TextView>(R.id.duration_display_value).text = durationText
        }
    }

    companion object {
        private const val RESOURCE_NOT_FOUND = Int.MIN_VALUE
    }
}