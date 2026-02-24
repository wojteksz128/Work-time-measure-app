package net.wojteksz128.worktimemeasureapp.window.dialog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.robot.base.BaseDialogRobot

class EditTextDialogRobot : BaseDialogRobot() {

    private val editText = withId(android.R.id.edit)
    private val okButton = withText("OK")
    private val cancelButton = withText("Cancel")

    fun enterText(text: String) {
        onViewInDialog(editText).perform(replaceText(text))
        Thread.sleep(500)
    }

    fun clickOk() {
        onViewInDialog(okButton).perform(click())
        Thread.sleep(500)
    }

    fun clickCancel() {
        onViewInDialog(cancelButton).perform(click())
        Thread.sleep(500)
    }

    override fun verifyIsDisplayed() {
        // Default implementation - can be overridden in subclasses
    }

    fun verifyErrorIsDisplayed(error: String) {
        onView(hasErrorText(error)).check(isDisplayed)
    }

    fun verifyOkButtonIsNotEnabled() {
        onView(okButton).check(isDisplayedAndNotEnabled)
    }
}
