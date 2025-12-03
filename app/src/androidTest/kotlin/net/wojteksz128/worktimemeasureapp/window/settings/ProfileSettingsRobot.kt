package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.window.dialog.EditImageRobot
import net.wojteksz128.worktimemeasureapp.window.dialog.EditTextDialogRobot

fun profileSettings(func: ProfileSettingsRobot.() -> Unit) = ProfileSettingsRobot().apply { func() }

class ProfileSettingsRobot : BaseScreenRobot() {
    private val usernamePreference = withText(R.string.settings_profile_username_title)
    private val emailPreference = withText(R.string.settings_profile_email_title)
    private val imagePreference = withText(R.string.image_view_preference_image_hint)
    private val editText = withId(android.R.id.edit)
    private val okButton = withText("OK")
    private val cancelButton = withText("Cancel")

    fun editUsername(func: EditTextDialogRobot.() -> Unit) {
        onView(usernamePreference).perform(click())
        EditTextDialogRobot().apply { func() }
    }

    fun editEmail(func: EditTextDialogRobot.() -> Unit) {
        onView(emailPreference).perform(click())
        EditTextDialogRobot().apply { func() }
    }

    fun editProfileImage(func: EditImageRobot.() -> Unit) {
        onView(imagePreference).perform(click())
        EditImageRobot().apply { func() }
    }

    fun goBack() {
        pressBack()
    }

    fun clickUsernamePreference() {
        onView(usernamePreference).perform(click())
    }

    fun clickEmailPreference() {
        onView(emailPreference).perform(click())
    }

    fun clickImagePreference() {
        onView(imagePreference).perform(click())
    }

    fun typeText(text: String) {
        onView(editText).perform(replaceText(text))
    }

    fun clickOk() {
        onView(okButton).perform(click())
    }

    fun clickCancel() {
        onView(cancelButton).perform(click())
    }

    override fun verifyIsDisplayed() {
        onView(usernamePreference).check(isDisplayed)
        onView(emailPreference).check(isDisplayed)
        onView(imagePreference).check(isDisplayed)
    }

    fun verifyErrorIsDisplayed(error: String) {
        onView(hasErrorText(error)).check(isDisplayed)
    }

    fun verifyOkButtonIsNotEnabled() {
        onView(okButton).check(isDisplayedAndNotEnabled)
    }
}
