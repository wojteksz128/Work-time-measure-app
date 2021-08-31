package net.wojteksz128.worktimemeasureapp.window

import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.BindingAdapter

object TextViewBinder {

    @JvmStatic
    @BindingAdapter("isDefined")
    fun isDefined(view: TextView, value: Boolean) {
        val currentTypeface = view.typeface
        view.setTypeface(currentTypeface, if (value) Typeface.NORMAL else Typeface.ITALIC)
    }
}