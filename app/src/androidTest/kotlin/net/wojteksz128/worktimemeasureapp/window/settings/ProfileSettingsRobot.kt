package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.idleMainThread
import net.wojteksz128.worktimemeasureapp.window.dialog.EditImageRobot
import net.wojteksz128.worktimemeasureapp.window.dialog.EditTextDialogRobot

fun profileSettings(func: ProfileSettingsRobot.() -> Unit) = ProfileSettingsRobot().apply { func() }

class ProfileSettingsRobot : BaseScreenRobot() {
    private val usernamePreference = withText(R.string.settings_profile_username_title)
    private val emailPreference = withText(R.string.settings_profile_email_title)
    private val imagePreference = withText(R.string.image_view_preference_image_hint)

    fun editUsername(func: EditTextDialogRobot.() -> Unit) {
        onView(usernamePreference).perform(click())
        EditTextDialogRobot(R.string.settings_profile_username_title).apply {
            waitForDialog()
            func()
        }
    }

    fun editEmail(func: EditTextDialogRobot.() -> Unit) {
        onView(emailPreference).perform(click())
        EditTextDialogRobot(R.string.settings_profile_email_title).apply {
            waitForDialog()
            func()
        }
    }

    fun editProfileImage(func: EditImageRobot.() -> Unit) {
        onView(imagePreference).perform(click())
        onView(isRoot()).perform(idleMainThread())
        EditImageRobot().apply { func() }
    }

    fun goBack() {
        pressBack()
        onView(isRoot()).perform(idleMainThread())
    }

    override fun verifyIsDisplayed() {
        onView(usernamePreference).check(isDisplayed)
        onView(emailPreference).check(isDisplayed)
        onView(imagePreference).check(isDisplayed)
    }
}
