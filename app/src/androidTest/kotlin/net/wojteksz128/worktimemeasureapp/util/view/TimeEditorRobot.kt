package net.wojteksz128.worktimemeasureapp.util.view

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.idleMainThread
import net.wojteksz128.worktimemeasureapp.util.setNumberOnNumberPicker
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class TimeEditorRobot(editor: Matcher<View?>) {
    private val hourDateTimePicker = allOf(withId(R.id.date_time_picker_hour), editor)
    private val minuteDateTimePicker = allOf(withId(R.id.date_time_picker_minute), editor)
    private val secondDateTimePicker = allOf(withId(R.id.date_time_picker_second), editor)
    private val acceptButton = allOf(withId(R.id.time_editor_accept), editor)
    private val amButton = allOf(withId(R.id.date_time_picker_am), editor)
    private val pmButton = allOf(withId(R.id.date_time_picker_pm), editor)
    private val dismissButton = allOf(withId(R.id.time_editor_dismiss), editor)

    fun setHour(hour: Int) {
        onView(hourDateTimePicker).inRoot(isDialog()).perform(setNumberOnNumberPicker(hour))
        onView(isRoot()).perform(idleMainThread())
    }

    fun setMinute(minute: Int) {
        onView(minuteDateTimePicker).inRoot(isDialog()).perform(setNumberOnNumberPicker(minute))
        onView(isRoot()).perform(idleMainThread())
    }

    fun setSecond(second: Int) {
        onView(secondDateTimePicker).inRoot(isDialog()).perform(setNumberOnNumberPicker(second))
        onView(isRoot()).perform(idleMainThread())
    }

    fun clickAccept() {
        onView(acceptButton).inRoot(isDialog()).perform(click())
    }

    fun clickAm() {
        onView(amButton).inRoot(isDialog()).perform(click())
        onView(isRoot()).perform(idleMainThread())
    }

    fun clickDismiss() {
        onView(dismissButton).inRoot(isDialog()).perform(click())
    }

    fun clickPm() {
        onView(pmButton).inRoot(isDialog()).perform(click())
        onView(isRoot()).perform(idleMainThread())
    }
}