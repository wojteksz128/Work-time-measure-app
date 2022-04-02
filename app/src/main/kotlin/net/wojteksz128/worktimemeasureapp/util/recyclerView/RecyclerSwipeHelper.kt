package net.wojteksz128.worktimemeasureapp.util.recyclerView

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue

abstract class RecyclerSwipeHelper(
    @ColorRes swipeLeftColorResId: Int,
    @DrawableRes swipeLeftIconResId: Int,
    @ColorRes swipeRightColorResId: Int,
    @DrawableRes swipeRightIconResId: Int,
    context: Context
) : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val swipeLeftColor = ContextCompat.getColor(context, swipeLeftColorResId)
    private val swipeLeftIcon = ContextCompat.getDrawable(context, swipeLeftIconResId)
        ?: throw Resources.NotFoundException("Swipe left icon not found!")

    private val swipeRightColor = ContextCompat.getColor(context, swipeRightColorResId)
    private val swipeRightIcon = ContextCompat.getDrawable(context, swipeRightIconResId)
        ?: throw Resources.NotFoundException("Swipe right icon not found!")

    private val background = ColorDrawable()

    private val intrinsicWidth: Int = swipeRightIcon.intrinsicWidth
    private val intrinsicHeight: Int = swipeRightIcon.intrinsicHeight

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        Log.d(
            RecyclerSwipeHelper::class.java.simpleName, """Params:
    c= $c
    recyclerView= $recyclerView
    dX= $dX
    dY= $dY
    actionState= $actionState
    isCurrentlyActive= $isCurrentlyActive
        """.trimIndent()
        )
        val itemView = viewHolder!!.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCancelled = (dX == 0f) and !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
            return
        }

        if (dX < 0) {
            background.apply {
                color = swipeLeftColor
                setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                draw(c)
            }

            val itemMargin = (itemHeight - intrinsicHeight) / 2
            val itemTop = itemView.top + itemMargin
            val itemRight = itemView.right - itemMargin
            val itemLeft = itemRight - intrinsicWidth
            val itemBottom = itemTop + intrinsicHeight

            var alpha =
                ((-itemView.translationX / itemView.width) * 510).toInt() // TODO: WFT is 510?!
            if (alpha > 255) alpha = 255

            swipeLeftIcon.apply {
                this.alpha = alpha
                setBounds(itemLeft, itemTop, itemRight, itemBottom)
                draw(c)
            }
        } else {
            background.apply {
                color = swipeRightColor
                setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
                draw(c)
            }

            val itemMargin = (itemHeight - intrinsicHeight) / 2
            val itemTop = itemView.top + itemMargin
            val itemLeft = itemView.left + itemMargin
            val itemRight = itemLeft + intrinsicWidth
            val itemBottom = itemTop + intrinsicHeight

            var alpha =
                ((-itemView.translationX / itemView.width).absoluteValue * 510).toInt() // TODO: WFT is 510?!
            if (alpha > 255) alpha = 255

            swipeRightIcon.apply {
                this.alpha = alpha
                setBounds(itemLeft, itemTop, itemRight, itemBottom)
                draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }
}