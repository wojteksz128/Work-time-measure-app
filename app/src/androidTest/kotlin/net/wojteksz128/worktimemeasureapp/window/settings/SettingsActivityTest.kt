package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Before
    fun setup() {
        hiltRule.inject()
        initialSettingsPreparer.initSettings()
    }

    @Test
    fun givenActivityStarted_whenNoSavedState_thenDisplaysHeaderFragment() {
        onView(withText(R.string.settings_header_profile_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnProfilePreference_thenDisplaysProfileFragment() {
        onView(withText(R.string.settings_header_profile_title)).perform(click())
        onView(withText(R.string.settings_profile_username_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnWorkTimePreference_thenDisplaysWorkTimeFragment() {
        onView(withText(R.string.settings_header_work_title)).perform(click())
        onView(withText(R.string.settings_workTime_notify_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnDaysOffPreference_thenDisplaysDaysOffFragment() {
        onView(withText(R.string.settings_header_daysOff_title)).perform(click())
        onView(withText(R.string.settings_daysOff_public_category_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnSyncPreference_thenDisplaysSyncFragment() {
        onView(withText(R.string.settings_header_sync_title)).perform(click())
        onView(withText(R.string.settings_sync_timeSync_category_title)).check(matches(isDisplayed()))
    }
}