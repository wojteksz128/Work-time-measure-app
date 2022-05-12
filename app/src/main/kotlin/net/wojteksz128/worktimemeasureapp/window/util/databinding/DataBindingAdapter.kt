package net.wojteksz128.worktimemeasureapp.window.util.databinding

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter

object DataBindingAdapter {
    @BindingAdapter("android:layout_height")
    @JvmStatic
    fun setHeight(view: View, expanded: Boolean) {
        val layoutParams = view.layoutParams
        layoutParams.height = if (expanded) ViewGroup.LayoutParams.WRAP_CONTENT else 0
        view.layoutParams = layoutParams
    }
}