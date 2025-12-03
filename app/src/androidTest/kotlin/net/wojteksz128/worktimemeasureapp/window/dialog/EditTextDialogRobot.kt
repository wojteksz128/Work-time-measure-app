package net.wojteksz128.worktimemeasureapp.window.dialog

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.robot.base.BaseDialogRobot

class EditTextDialogRobot : BaseDialogRobot() {

    private val editText = withId(android.R.id.edit)
    private val okButton = withText("OK")

    fun enterText(text: String) {
        onViewInDialog(editText).perform(replaceText(text))
    }

    fun clickOk() {
        onViewInDialog(okButton).perform(click())
    }

    override fun verifyIsDisplayed() {
        // Default implementation - can be overridden in subclasses
    }
}
