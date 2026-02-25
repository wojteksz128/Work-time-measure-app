package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import org.hamcrest.Matchers.equalTo

fun weekWorkTimeSettings(func: WeekWorkTimeSettingsRobot.() -> Unit) =
    WeekWorkTimeSettingsRobot().apply { func() }

class WeekWorkTimeSettingsRobot : BaseScreenRobot() {
    private val firstWeekDayTitle = withText(R.string.settings_workTime_week_firstWeekDay_title)
    private val daysOfWorkingWeekTitle =
        withText(R.string.settings_workTime_week_daysOfWorkingWeek_title)
    private val durationTitle = withText(R.string.settings_workTime_week_duration_title)
    private val timePicker = withClassName(equalTo("android.widget.TimePicker"))
    private val okButton = withText(android.R.string.ok)

    fun changeFirstDayOfWeek(entry: Int) {
        onView(firstWeekDayTitle).perform(click())
        Thread.sleep(500)
        onView(withText(entry)).perform(click())
        Thread.sleep(500)
    }

    fun changeDaysOfWorkingWeek(vararg entries: Int) {
        onView(daysOfWorkingWeekTitle).perform(click())
        Thread.sleep(500)
        entries.forEach {
            onView(withText(it)).perform(click())
            Thread.sleep(500)
        }
        onView(okButton).perform(click())
        Thread.sleep(500)
    }

    fun changeWorkTimeInDay(hour: Int, minute: Int) {
        onView(durationTitle).perform(click())
        Thread.sleep(500)
        onView(timePicker).perform(PickerActions.setTime(hour, minute))
        Thread.sleep(500)
        onView(okButton).perform(click())
        Thread.sleep(500)
    }

    override fun verifyIsDisplayed() {
        onView(firstWeekDayTitle).check(isDisplayed)
        onView(daysOfWorkingWeekTitle).check(isDisplayed)
        onView(durationTitle).check(isDisplayed)
    }
}
