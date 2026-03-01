package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.idleMainThread
import net.wojteksz128.worktimemeasureapp.util.waitForDialogView
import org.hamcrest.Matchers.equalTo

fun weekWorkTimeSettings(func: WeekWorkTimeSettingsRobot.() -> Unit) =
    WeekWorkTimeSettingsRobot().apply { func() }

// TODO 28.02.2026 Separate robot classes for dialogs on this screen
class WeekWorkTimeSettingsRobot : BaseScreenRobot() {
    private val firstWeekDayTitle = withText(R.string.settings_workTime_week_firstWeekDay_title)
    private val daysOfWorkingWeekTitle =
        withText(R.string.settings_workTime_week_daysOfWorkingWeek_title)
    private val durationTitle = withText(R.string.settings_workTime_week_duration_title)
    private val timePicker = withClassName(equalTo("android.widget.TimePicker"))
    private val okButton = withText(android.R.string.ok)

    fun changeFirstDayOfWeek(entry: Int) {
        val entryMatcher = withText(entry)
        onView(firstWeekDayTitle).perform(click())
        waitForDialogView(entryMatcher)
        onView(entryMatcher).perform(click())
    }

    fun changeDaysOfWorkingWeek(vararg entries: Int) {
        onView(daysOfWorkingWeekTitle).perform(click())
        waitForDialogView(withText(entries.first()))
        entries.forEach {
            onView(withText(it)).perform(click())
            onView(isRoot()).perform(idleMainThread())
        }
        onView(okButton).perform(click())
    }

    fun changeWorkTimeInDay(hour: Int, minute: Int) {
        onView(durationTitle).perform(click())
        waitForDialogView(timePicker)
        onView(timePicker).perform(PickerActions.setTime(hour, minute))
        onView(isRoot()).perform(idleMainThread())
        onView(okButton).perform(click())
    }

    override fun verifyIsDisplayed() {
        onView(firstWeekDayTitle).check(isDisplayed)
        onView(daysOfWorkingWeekTitle).check(isDisplayed)
        onView(durationTitle).check(isDisplayed)
    }
}
