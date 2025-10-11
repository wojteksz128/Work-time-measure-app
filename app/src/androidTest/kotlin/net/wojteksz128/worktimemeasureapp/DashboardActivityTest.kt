package net.wojteksz128.worktimemeasureapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DashboardActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun launchActivity() {
        ActivityScenario.launch(DashboardActivity::class.java)
    }

    @Test
    fun test_activityInView() {
        onView(withId(R.id.dashboard_content)).check(matches(isDisplayed()))
    }

    @Test
    fun test_fabIsDisplayedAndClickable() {
        onView(withId(R.id.dashboard_enter_fab)).check(matches(isDisplayed()))
        onView(withId(R.id.dashboard_enter_fab)).perform(click())
    }

    @Test
    fun test_navigationDrawer() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToHistory() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history))
    }

    @Test
    fun test_navigationToSettings() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings))
    }
}