package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WorkTimeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun givenFragmentStarted_thenDisplaysWorkTimePreferences() {
        launchFragmentInHiltContainer<WorkTimeFragment>()

        onView(withText(R.string.settings_workTime_notify_title)).check(matches(isDisplayed()))
        onView(withText(R.string.settings_workTime_week_title)).check(matches(isDisplayed()))
    }
}