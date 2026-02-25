package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot

fun workTimeSettings(func: WorkTimeSettingsRobot.() -> Unit) =
    WorkTimeSettingsRobot().apply { func() }

class WorkTimeSettingsRobot : BaseScreenRobot() {
    private val notifyPreference = withText(R.string.settings_workTime_notify_title)
    private val weekPreference = withText(R.string.settings_workTime_week_title)

    fun toggleNotify() {
        onView(notifyPreference).perform(click())
        Thread.sleep(500)
    }

    fun openWeekSettings(func: WeekWorkTimeSettingsRobot.() -> Unit) {
        onView(weekPreference).perform(click())
        Thread.sleep(500)
        WeekWorkTimeSettingsRobot().apply { func() }
    }

    override fun verifyIsDisplayed() {
        onView(notifyPreference).check(isDisplayed)
        onView(weekPreference).check(isDisplayed)
    }
}
