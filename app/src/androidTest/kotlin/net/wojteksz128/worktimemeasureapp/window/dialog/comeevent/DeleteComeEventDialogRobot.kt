package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseDialogRobot

fun deleteComeEventDialog(func: DeleteComeEventDialogRobot.() -> Unit) =
    DeleteComeEventDialogRobot().apply { func() }

class DeleteComeEventDialogRobot : BaseDialogRobot() {

    private val cancelButton = withText(R.string.delete_come_event_dialog_action_cancel)
    private val dialogTitle = withText(R.string.delete_come_event_dialog_title)
    private val deleteButton = withText(R.string.delete_come_event_dialog_action_delete)

    private val doesNotExist = doesNotExist()

    override fun verifyIsDisplayed() {
        onViewInDialog(dialogTitle).check(isDisplayed)
    }

    fun verifyMessage(expectedMessage: String) {
        onViewInDialog(withText(expectedMessage)).check(isDisplayed)
    }

    fun clickDelete() {
        onViewInDialog(deleteButton).perform(click())
    }

    fun clickCancel() {
        onViewInDialog(cancelButton).perform(click())
    }

    fun verifyDeleteDialogIsDismissed() {
        onView(dialogTitle).check(doesNotExist)
    }
}
