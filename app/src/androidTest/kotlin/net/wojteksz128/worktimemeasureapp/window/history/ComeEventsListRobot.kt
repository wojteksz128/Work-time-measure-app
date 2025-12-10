package net.wojteksz128.worktimemeasureapp.window.history

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.DeleteComeEventDialogRobot
import net.wojteksz128.worktimemeasureapp.window.dialog.comeevent.EditComeEventDialogRobot
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder

class ComeEventsListRobot(recyclerViewId: Int) {

    private val eventsList = withId(recyclerViewId)

    fun swipeLeftOnEvent(position: Int, func: EditComeEventDialogRobot.() -> Unit) {
        onView(eventsList).perform(
            actionOnItemAtPosition<ComeEventViewHolder>(
                position, swipeLeft()
            )
        )
        EditComeEventDialogRobot().apply { func() }
    }

    fun swipeRightOnEvent(position: Int, func: DeleteComeEventDialogRobot.() -> Unit) {
        onView(eventsList).perform(
            actionOnItemAtPosition<ComeEventViewHolder>(
                position, swipeRight()
            )
        )
        DeleteComeEventDialogRobot().apply { func() }
    }
}
