package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WorkTimeFragmentTest {

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
    fun givenFragmentStarted_thenDisplaysWorkTimePreferences() {
        launchFragmentInHiltContainer<WorkTimeFragment>()

        onView(withText(R.string.settings_workTime_notify_title)).check(matches(isDisplayed()))
        onView(withText(R.string.settings_workTime_week_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenNotificationSwitchIsToggled_thenValueIsSaved() {
        val key = context.getString(R.string.settings_key_workTime_notify_enable)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val initialValue = sharedPreferences.getBoolean(key, false)

        launchFragmentInHiltContainer<WorkTimeFragment>()

        onView(withText(R.string.settings_workTime_notify_title)).perform(click())

        val newValue = sharedPreferences.getBoolean(key, false)
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.WorkTime.NotifyingEnabled.value)
    }

    @Test
    fun whenWeekWorkTimeIsClicked_thenNavigatesToWeekWorkTimeFragment() {
        launchFragmentInHiltContainer<WorkTimeFragment>()

        onView(withText(R.string.settings_workTime_week_title)).perform(click())

        onView(withText(R.string.settings_workTime_week_firstWeekDay_title)).check(
            matches(
                isDisplayed()
            )
        )
    }
}