package net.wojteksz128.worktimemeasureapp.util.recyclerView

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

abstract class RecyclerSwipeActionParam(
    @ColorRes backgroundColorResId: Int,
    @DrawableRes iconResId: Int,
    context: Context,
    val action: () -> Unit
) {
    val backgroundColor = ContextCompat.getColor(context, backgroundColorResId)
    val icon = ContextCompat.getDrawable(context, iconResId)
        ?: throw Resources.NotFoundException("Swipe icon not found!")

    abstract fun calculateBackgroundRect(itemView: View, dX: Float): Rect
    abstract fun calculateAlpha(itemView: View): Int
    abstract fun calculateIconRect(itemView: View): Rect
}