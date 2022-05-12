package net.wojteksz128.worktimemeasureapp.util.recyclerView

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

class RecyclerRightSwipeActionParam<Entity>(
    @ColorRes backgroundColorResId: Int,
    @DrawableRes iconResId: Int,
    context: Context,
    action: (Entity, Int) -> Unit
) : RecyclerSwipeActionParam<Entity>(backgroundColorResId, iconResId, context, action) {

    override fun calculateBackgroundRect(itemView: View, dX: Float) =
        Rect(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)

    override fun calculateAlpha(itemView: View): Int {
        var alpha =
            ((itemView.translationX / itemView.width) * 510).toInt() // TODO: WFT is 510?!
        if (alpha > 255) alpha = 255
        return alpha
    }

    override fun calculateIconRect(itemView: View): Rect {
        val itemHeight = itemView.bottom - itemView.top
        val itemMargin = (itemHeight - icon.intrinsicHeight) / 2
        val itemTop = itemView.top + itemMargin
        val itemLeft = itemView.left + itemMargin
        val itemRight = itemLeft + icon.intrinsicWidth
        val itemBottom = itemTop + icon.intrinsicHeight
        return Rect(itemLeft, itemTop, itemRight, itemBottom)
    }
}