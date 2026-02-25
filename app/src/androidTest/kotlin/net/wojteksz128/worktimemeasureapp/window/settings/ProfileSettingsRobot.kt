package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
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

    fun editUsername(func: EditTextDialogRobot.() -> Unit) {
        onView(usernamePreference).perform(click())
        Thread.sleep(500)
        EditTextDialogRobot().apply { func() }
    }

    fun editEmail(func: EditTextDialogRobot.() -> Unit) {
        onView(emailPreference).perform(click())
        Thread.sleep(500)
        EditTextDialogRobot().apply { func() }
    }

    fun editProfileImage(func: EditImageRobot.() -> Unit) {
        onView(imagePreference).perform(click())
        Thread.sleep(500)
        EditImageRobot().apply { func() }
    }

    fun goBack() {
        pressBack()
        Thread.sleep(500)
    }

    override fun verifyIsDisplayed() {
        onView(usernamePreference).check(isDisplayed)
        onView(emailPreference).check(isDisplayed)
        onView(imagePreference).check(isDisplayed)
    }
}
