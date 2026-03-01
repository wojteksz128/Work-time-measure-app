package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.waitForView

fun backupSettings(func: BackupSettingsRobot.() -> Unit) = BackupSettingsRobot().apply { func() }

class BackupSettingsRobot : BaseScreenRobot() {

    private val exportImportCategoryTitle = withText(R.string.settings_backup_export_import)
    private val sharingCategoryTitle = withText(R.string.settings_backup_sharing)
    private val exportTitle = withText(R.string.settings_backup_export_title)
    private val importTitle = withText(R.string.settings_backup_import_title)
    private val shareTitle = withText(R.string.settings_backup_share_title)
    private val snackbar = withId(com.google.android.material.R.id.snackbar_text)

    override fun verifyIsDisplayed() {
        onView(exportImportCategoryTitle).check(isDisplayed)
        onView(exportTitle).check(isDisplayed)
        onView(importTitle).check(isDisplayed)
        onView(sharingCategoryTitle).check(isDisplayed)
        onView(shareTitle).check(isDisplayed)
    }

    fun clickExport() {
        onView(exportTitle).perform(click())
    }

    fun clickImport() {
        onView(importTitle).perform(click())
    }

    fun clickShare() {
        onView(shareTitle).perform(click())
    }

    fun verifySnackbarIsDisplayed() {
        onView(isRoot()).perform(waitForView(snackbar))
        onView(snackbar).check(isDisplayed)
    }

    fun verifySnackbarWithText(text: String) {
        onView(withText(text)).check(isDisplayed)
    }

    fun onImportStrategyDialog(func: ImportStrategyDialogRobot.() -> Unit) {
        ImportStrategyDialogRobot().apply {
            waitForDialog()
            func()
        }
    }
}

