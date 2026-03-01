package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseDialogRobot
import net.wojteksz128.worktimemeasureapp.util.idleMainThread
import net.wojteksz128.worktimemeasureapp.util.waitForDialogView

class ImportStrategyDialogRobot : BaseDialogRobot() {

    private val dialogTitle = withText(R.string.settings_backup_import_strategy_title)
    private val strategyMergeText = withText(R.string.settings_backup_import_strategy_merge)
    private val strategyReplaceText = withText(R.string.settings_backup_import_strategy_replace)
    private val strategySkipText = withText(R.string.settings_backup_import_strategy_skip)
    private val importButton = withText(R.string.settings_backup_import_action_import)
    private val cancelButton = withText(android.R.string.cancel)

    override fun waitForDialog() {
        waitForDialogView(dialogTitle)
    }

    override fun verifyIsDisplayed() {
        onView(dialogTitle).check(isDisplayed)
        onView(strategyMergeText).check(isDisplayed)
        onView(strategyReplaceText).check(isDisplayed)
        onView(strategySkipText).check(isDisplayed)
    }

    fun selectMerge() {
        onView(strategyMergeText).perform(click())
        onView(isRoot()).perform(idleMainThread())
    }

    fun selectReplace() {
        onView(strategyReplaceText).perform(click())
        onView(isRoot()).perform(idleMainThread())
    }

    fun selectSkip() {
        onView(strategySkipText).perform(click())
        onView(isRoot()).perform(idleMainThread())
    }

    fun confirmImport() {
        onView(importButton).perform(click())
    }

    fun cancelImport() {
        onView(cancelButton).perform(click())
    }
}
