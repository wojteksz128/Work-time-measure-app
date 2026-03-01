package net.wojteksz128.worktimemeasureapp.window.dialog

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.robot.base.BaseDialogRobot
import net.wojteksz128.worktimemeasureapp.util.waitForDialogView

class EditTextDialogRobot(@param:StringRes private val dialogTitleText: Int) : BaseDialogRobot() {

    private val editText = withId(android.R.id.edit)
    private val okButton = withText("OK")
    private val cancelButton = withText("Cancel")
    private val dialogTitle = withText(dialogTitleText)

    override fun waitForDialog() {
        waitForDialogView(dialogTitle)
    }

    fun enterText(text: String) {
        onViewInDialog(editText).perform(replaceText(text))
    }

    fun clickOk() {
        onViewInDialog(okButton).perform(click())
    }

    fun clickCancel() {
        onViewInDialog(cancelButton).perform(click())
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
