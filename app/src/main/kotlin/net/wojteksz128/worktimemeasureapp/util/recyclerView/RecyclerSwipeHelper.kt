package net.wojteksz128.worktimemeasureapp.util.recyclerView

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.wojteksz128.worktimemeasureapp.model.DomainModel

class RecyclerSwipeHelper<Entity : DomainModel, VH : ViewHolder>(
    private val swipeLeft: RecyclerLeftSwipeActionParam<Entity, VH>,
    private val swipeRight: RecyclerRightSwipeActionParam<Entity, VH>,
    private val entityExtractor: (VH) -> Entity
) : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val background = ColorDrawable()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean = false

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder!!.itemView
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
            drawActionSlide(swipeLeft, itemView, dX, c)
        } else {
            drawActionSlide(swipeRight, itemView, dX, c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawActionSlide(
        swipeActionParam: RecyclerSwipeActionParam<Entity, VH>,
        itemView: View,
        dX: Float,
        c: Canvas
    ) {
        background.apply {
            color = swipeActionParam.backgroundColor
            bounds = swipeActionParam.calculateBackgroundRect(itemView, dX)
            draw(c)
        }

        swipeActionParam.icon.apply {
            this.alpha = swipeActionParam.calculateAlpha(itemView)
            bounds = swipeActionParam.calculateIconRect(itemView)
            draw(c)
        }
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val bindingAdapterPosition = viewHolder.bindingAdapterPosition

        @Suppress("UNCHECKED_CAST")
        val viewHolderInformation = ViewHolderInformation(
            viewHolder as VH,
            bindingAdapterPosition,
            viewHolder.bindingAdapter as RecyclerView.Adapter<VH>
        )
        val entity = entityExtractor(viewHolder)

        when (direction) {
            LEFT -> swipeLeft.action(entity, viewHolderInformation)
            RIGHT -> swipeRight.action(entity, viewHolderInformation)
        }
    }
}