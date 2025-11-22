package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.DayOfWeek
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WeekWorkTimeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        initialSettingsPreparer.initSettings()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysWeekWorkTimePreferences() {
        launchFragmentInHiltContainer<WeekWorkTimeFragment>()

        onView(withText(R.string.settings_workTime_week_firstWeekDay_title)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withText(R.string.settings_workTime_week_daysOfWorkingWeek_title)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withText(R.string.settings_workTime_week_duration_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenFirstDayOfWeekIsChanged_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_firstWeekDay)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getString(key, null)

        launchFragmentInHiltContainer<WeekWorkTimeFragment>()

        onView(withText(R.string.settings_workTime_week_firstWeekDay_title)).perform(click())
        onView(withText(R.string.settings_workTime_week_firstWeekDay_sundayEntry)).perform(click())

        val newValue = sharedPreferences.getString(key, null)
        assertNotEquals(initialValue, newValue)
        assertEquals(DayOfWeek.SUNDAY.name, newValue)
        assertEquals(newValue, Settings.WorkTime.Week.FirstWeekDay.value)
    }

    @Test
    fun whenDaysOfWorkingWeekIsClicked_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_week_daysOfWorkingWeek)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getStringSet(key, emptySet())

        launchFragmentInHiltContainer<WeekWorkTimeFragment>()

        onView(withText(R.string.settings_workTime_week_daysOfWorkingWeek_title)).perform(click())
        onView(withText(R.string.settings_workTime_week_firstWeekDay_mondayEntry)).perform(click())
        onView(withText(R.string.settings_workTime_week_firstWeekDay_wednesdayEntry)).perform(click())
        onView(withText(android.R.string.ok)).perform(click())

        val newValue = sharedPreferences.getStringSet(key, emptySet())
        assertNotEquals(initialValue, newValue)
        newValue!!.let {
            assertFalse(it.contains(DayOfWeek.MONDAY.name))
            assertFalse(it.contains(DayOfWeek.WEDNESDAY.name))
        }
        assertEquals(newValue, Settings.WorkTime.Week.DaysOfWorkingWeek.value)
    }

    @Test
    fun whenWorkTimeInDayIsChanged_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_duration)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getInt(key, 0)

        launchFragmentInHiltContainer<WeekWorkTimeFragment>()

        onView(withText(R.string.settings_workTime_week_duration_title)).perform(click())

        onView(withClassName(equalTo("android.widget.TimePicker"))).perform(
            PickerActions.setTime(
                10,
                15
            )
        )
        onView(withText(android.R.string.ok)).perform(click())

        val newValue = sharedPreferences.getInt(key, 0)

        assertNotEquals(initialValue, newValue)
        assertEquals(10 * 60 + 15, newValue)
        assertEquals(newValue.toLong(), Settings.WorkTime.Week.Duration.value.toMinutes())
    }
}