package net.wojteksz128.worktimemeasureapp.window

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class BaseActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dayOffService: DayOffService

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    private lateinit var scenario: ActivityScenario<DashboardActivity>

    @Before
    fun setup() {
        hiltRule.inject()

        // Initialize application settings first to ensure correct values are loaded
        initialSettingsPreparer.initSettings()

        dayOffService.stub {
            onBlocking { getDayType(any<ZonedDateTime>()) } doReturn DayType.WorkDay
            onBlocking { getDayType(any<LocalDate>()) } doReturn DayType.WorkDay
        }

        scenario = ActivityScenario.launch(DashboardActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun test_toolbarAndHamburgerMenuAreVisible() {
        onView(withId(R.id.base_toolbar)).check(matches(isDisplayed()))
        onView(withContentDescription(R.string.navigation_drawer_open)).check(matches(isDisplayed()))
    }

    @Test
    fun test_hamburgerMenuOpensDrawer() {
        onView(withContentDescription(R.string.navigation_drawer_open)).perform(click())
        onView(withId(R.id.base_nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToHome() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home))
        onView(withId(R.id.dashboard_content)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToHistory() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history))
        onView(withId(R.id.history_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToDaysOffList() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_days_off_list))
    }

    @Test
    fun test_navigationToSettings() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings))
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
    }

    @Test
    fun test_aboutSnackbar() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_about))
        onView(withText(R.string.about)).check(matches(isDisplayed()))
    }
}
