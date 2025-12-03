package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.setSwitchTo
import org.hamcrest.CoreMatchers.allOf

fun syncSettings(func: SyncSettingsRobot.() -> Unit) = SyncSettingsRobot().apply { func() }

class SyncSettingsRobot : BaseScreenRobot() {
    private val timeSyncCategoryTitle = withText(R.string.settings_sync_timeSync_category_title)
    private val enableTitle = withText(R.string.settings_sync_timeSync_enable_title)
    private val serverTitle = withText(R.string.settings_sync_timeSync_server_title)
    private val enableClickable = allOf(isClickable(), hasDescendant(enableTitle))
    private val editText = withId(android.R.id.edit)
    private val okButton = withText("OK")

    fun setTimeSync(enabled: Boolean) {
        onView(enableClickable).perform(setSwitchTo(enabled))
    }

    fun toggleTimeSync() {
        onView(enableTitle).perform(click())
    }

    fun changeServer(server: String) {
        onView(serverTitle).perform(click())
        onView(editText).perform(replaceText(server))
        onView(okButton).perform(click())
    }

    override fun verifyIsDisplayed() {
        onView(timeSyncCategoryTitle).check(isDisplayed)
    }

    fun verifySyncDisabled() {
        onView(serverTitle).check(isDisplayedAndNotEnabled)
    }

    fun verifySyncEnabled() {
        onView(serverTitle).check(isDisplayedAndEnabled)
    }
}
