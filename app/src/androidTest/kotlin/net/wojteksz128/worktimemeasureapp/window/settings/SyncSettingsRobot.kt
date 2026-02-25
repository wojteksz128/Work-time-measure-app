package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.setSwitchTo
import net.wojteksz128.worktimemeasureapp.window.dialog.EditTextDialogRobot
import org.hamcrest.CoreMatchers.allOf

fun syncSettings(func: SyncSettingsRobot.() -> Unit) = SyncSettingsRobot().apply { func() }

class SyncSettingsRobot : BaseScreenRobot() {
    private val timeSyncCategoryTitle = withText(R.string.settings_sync_timeSync_category_title)
    private val enableTitle = withText(R.string.settings_sync_timeSync_enable_title)
    private val serverTitle = withText(R.string.settings_sync_timeSync_server_title)
    private val enableClickable = allOf(isClickable(), hasDescendant(enableTitle))

    fun setTimeSync(enabled: Boolean) {
        onView(enableClickable).perform(setSwitchTo(enabled))
        Thread.sleep(500)
    }

    fun toggleTimeSync() {
        onView(enableTitle).perform(click())
        Thread.sleep(500)
    }

    fun editServer(func: EditTextDialogRobot.() -> Unit) {
        onView(serverTitle).perform(click())
        Thread.sleep(500)
        EditTextDialogRobot().apply { func() }
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
