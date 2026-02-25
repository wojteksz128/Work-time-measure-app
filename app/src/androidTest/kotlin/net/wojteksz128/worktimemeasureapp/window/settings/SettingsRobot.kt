package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot

fun settings(func: SettingsRobot.() -> Unit) = SettingsRobot().apply { func() }

class SettingsRobot : BaseScreenRobot() {
    private val profilePreference = withText(R.string.settings_header_profile_title)
    private val workTimePreference = withText(R.string.settings_header_work_title)
    private val daysOffPreference = withText(R.string.settings_header_daysOff_title)
    private val syncPreference = withText(R.string.settings_header_sync_title)
    private val settingsView = withId(R.id.settings)

    fun openProfileSettings(func: ProfileSettingsRobot.() -> Unit) {
        onView(profilePreference).perform(click())
        Thread.sleep(500)
        ProfileSettingsRobot().apply { func() }
    }

    fun openWorkTimeSettings(func: WorkTimeSettingsRobot.() -> Unit) {
        onView(workTimePreference).perform(click())
        Thread.sleep(500)
        WorkTimeSettingsRobot().apply { func() }
    }

    fun openDaysOffSettings(func: DaysOffSettingsRobot.() -> Unit) {
        onView(daysOffPreference).perform(click())
        Thread.sleep(500)
        DaysOffSettingsRobot().apply { func() }
    }

    fun openSyncSettings(func: SyncSettingsRobot.() -> Unit) {
        onView(syncPreference).perform(click())
        Thread.sleep(500)
        SyncSettingsRobot().apply { func() }
    }

    fun openBackupSettings(func: BackupSettingsRobot.() -> Unit) {
        onView(withText(R.string.settings_header_backup_title)).perform(click())
        Thread.sleep(500)
        BackupSettingsRobot().apply { func() }
    }

    override fun verifyIsDisplayed() {
        onView(settingsView).check(isDisplayed)
    }

    fun verifyHeaderFragmentIsDisplayed() {
        onView(profilePreference).check(isDisplayed)
    }

    fun goBack() {
        pressBack()
        Thread.sleep(500)
    }
}
