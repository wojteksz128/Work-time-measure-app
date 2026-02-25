package net.wojteksz128.worktimemeasureapp.window.dialog.comeevent

import android.text.format.DateFormat.is24HourFormat
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseDialogRobot
import net.wojteksz128.worktimemeasureapp.util.datetime.AmPm
import net.wojteksz128.worktimemeasureapp.util.datetime.convert24To12HourFormat
import net.wojteksz128.worktimemeasureapp.util.view.TimeEditorRobot
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

fun editComeEventDialog(func: EditComeEventDialogRobot.() -> Unit) =
    EditComeEventDialogRobot().apply { func() }

class EditComeEventDialogRobot : BaseDialogRobot() {

    private val is24HourFormat by lazy { is24HourFormat(getInstrumentation().targetContext) }

    private val timeEditorValue = withId(R.id.time_editor_value)
    private val startTimeEditor = isDescendantOfA(withId(R.id.component_time_editor_start))
    private val finishTimeEditor = isDescendantOfA(withId(R.id.component_time_editor_finish))
    private val dialogTitle = withText(R.string.edit_come_event_dialog_title)
    private val finishTimeValue = allOf(timeEditorValue, finishTimeEditor)
    private val startTimeValue = allOf(timeEditorValue, startTimeEditor)
    private val cancelButton = withId(android.R.id.button2)
    private val clearButton = allOf(withId(R.id.time_editor_clear_time), finishTimeEditor)
    private val okButton = withId(android.R.id.button1)
    private val setFinishTimeEditor = allOf(withId(R.id.time_editor_set_time), finishTimeEditor)
    private val setStartTimeEditor = allOf(withId(R.id.time_editor_set_time), startTimeEditor)

    private val haveText = { text: String -> matches(withText(text)) }
    private val isDisabled = matches(not(isEnabled()))
    private val isEnabled = matches(isEnabled())

    fun setStartTime(hour: Int, minute: Int, second: Int = 0) {
        openStartTimeEditor {
            setTime(hour, minute, second)
        }
    }

    fun setFinishTime(hour: Int, minute: Int, second: Int = 0) {
        openFinishTimeEditor {
            setTime(hour, minute, second)
        }
    }

    fun clearFinishTime() {
        onViewInDialog(clearButton).perform(click())
        Thread.sleep(500)
    }

    fun clickOk() {
        onViewInDialog(okButton).perform(click())
        Thread.sleep(500)
    }

    fun clickCancel() {
        onViewInDialog(cancelButton).perform(click())
        Thread.sleep(500)
    }

    override fun verifyIsDisplayed() {
        onViewInDialog(dialogTitle).check(isDisplayed)
    }

    fun verifyStartTime(expectedTime: String) {
        onViewInDialog(startTimeValue).check(haveText(expectedTime))
    }

    fun verifyFinishTime(expectedTime: String) {
        onViewInDialog(finishTimeValue).check(haveText(expectedTime))
    }

    fun verifyOkButtonIsEnabled() {
        onViewInDialog(okButton).check(isEnabled)
    }

    fun verifyOkButtonIsDisabled() {
        onViewInDialog(okButton).check(isDisabled)
    }

    fun openStartTimeEditor(func: TimeEditorRobot.() -> Unit) {
        onViewInDialog(setStartTimeEditor).perform(click())
        Thread.sleep(500)
        TimeEditorRobot(startTimeEditor).apply { func() }
    }

    fun openFinishTimeEditor(func: TimeEditorRobot.() -> Unit) {
        onViewInDialog(setFinishTimeEditor).perform(click())
        Thread.sleep(500)
        TimeEditorRobot(finishTimeEditor).apply { func() }
    }

    private fun TimeEditorRobot.setTime(
        hour: Int,
        minute: Int,
        second: Int,
    ) {
        if (is24HourFormat)
            setHour(hour)
        else {
            val (hour12Format, amPm) = convert24To12HourFormat(hour)
            setHour(hour12Format)
            if (amPm == AmPm.AM) clickAm()
            else clickPm()
        }
        setMinute(minute)
        setSecond(second)
        clickAccept()
    }
}
