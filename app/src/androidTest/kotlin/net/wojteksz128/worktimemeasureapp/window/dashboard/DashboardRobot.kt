package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.robot.base.BaseScreenRobot
import net.wojteksz128.worktimemeasureapp.util.isDefaultImage
import net.wojteksz128.worktimemeasureapp.util.withItemCount
import net.wojteksz128.worktimemeasureapp.window.dayoff.DaysOffListRobot
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsAdapter.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventsListRobot
import net.wojteksz128.worktimemeasureapp.window.history.HistoryRobot
import net.wojteksz128.worktimemeasureapp.window.settings.SettingsRobot
import org.hamcrest.CoreMatchers.not

fun dashboard(func: DashboardRobot.() -> Unit) = DashboardRobot().apply { func() }

class DashboardRobot : BaseScreenRobot() {

    private val comeEventsListRobot = ComeEventsListRobot(R.id.dashboard_current_day_events_list)
    private val aboutSnackbarText = withText(R.string.about)
    private val content = withId(R.id.dashboard_content)
    private val drawer = withId(R.id.base_drawer_layout)
    private val drawerNavigation = withId(R.id.base_nav_view)
    private val emptyEventsMessage = withId(R.id.dashboard_current_day_empty_events_message)
    private val enterFab = withId(R.id.dashboard_enter_fab)
    private val eventsList = withId(R.id.dashboard_current_day_events_list)
    private val hamburgerMenu = withContentDescription(R.string.navigation_drawer_open)
    private val remainingDayTime = withId(R.id.dashboard_remaining_day_time)
    private val todayWorkTime = withId(R.id.dashboard_today_work_time)

    private val toolbar = withId(R.id.base_toolbar)
    private val hasDescendantWithText = { text: String -> matches(hasDescendant(withText(text))) }
    private val hasNoDescendantWithText =
        { text: String -> matches(not(hasDescendant(withText(text)))) }

    private val withItemCount = { count: Int -> matches(withItemCount(count)) }

    override fun verifyIsDisplayed() {
        onView(content).check(isDisplayed)
    }

    fun clickFab() {
        onView(enterFab).perform(click())
    }

    fun verifyFabIsDisplayed() {
        onView(enterFab).check(isDisplayed)
    }

    fun verifyInitialState() {
        onView(emptyEventsMessage).check(isDisplayed)
        onView(eventsList).check(isNotDisplayed)
        onView(remainingDayTime).check(hasDescendantWithText("8:00:00"))
        onView(todayWorkTime).check(hasDescendantWithText("0:00:00"))
    }

    fun verifyWorkStartedState() {
        onView(emptyEventsMessage).check(isNotDisplayed)
        onView(eventsList).check(isDisplayed)
        onView(eventsList).check(withItemCount(1))
    }

    fun verifySnackbarIsShown(message: String) {
        onView(withText(message)).check(isDisplayed)
    }

    fun verifyAboutSnackbarIsDisplayed() {
        onView(aboutSnackbarText).check(isDisplayed)
    }

    fun verifyTimersHaveStarted() {
        onView(remainingDayTime).check(hasNoDescendantWithText("8:00:00"))
        onView(todayWorkTime).check(hasNoDescendantWithText("0:00:00"))
    }

    fun eventsRecyclerView(func: ComeEventsListRobot.() -> Unit) {
        comeEventsListRobot.apply { func() }
    }

    fun verifyEventListCount(count: Int) {
        onView(eventsList).check(withItemCount(count))
    }

    fun scrollToEvent(position: Int) {
        onView(eventsList)
            .perform(scrollToPosition<ComeEventViewHolder>(position))
    }

    fun openNavigationDrawer() {
        onView(drawer).perform(open())
    }

    private fun navigateTo(navId: Int) {
        onView(drawerNavigation).perform(NavigationViewActions.navigateTo(navId))
    }

    fun navigateToSettings(func: SettingsRobot.() -> Unit) {
        openNavigationDrawer()
        navigateTo(R.id.nav_settings)
        SettingsRobot().apply { func() }
    }

    fun navigateToHistory(func: HistoryRobot.() -> Unit) {
        openNavigationDrawer()
        navigateTo(R.id.nav_history)
        HistoryRobot().apply { func() }
    }

    fun navigateToDaysOffList(composeTestRule: ComposeTestRule, func: DaysOffListRobot.() -> Unit) {
        openNavigationDrawer()
        navigateTo(R.id.nav_days_off_list)
        DaysOffListRobot(composeTestRule).apply { func() }
    }

    fun navigateToAbout() {
        openNavigationDrawer()
        navigateTo(R.id.nav_about)
    }

    fun navigateToHome(func: DashboardRobot.() -> Unit) {
        openNavigationDrawer()
        navigateTo(R.id.nav_home)
        this.apply { func() }
    }

    fun verifyToolbarAndHamburgerAreVisible() {
        onView(toolbar).check(isDisplayed)
        onView(hamburgerMenu).check(isDisplayed)
    }

    fun verifyDrawerIsOpen() {
        onView(drawerNavigation).check(isDisplayed)
    }

    fun verifyDrawerUsername(username: String) {
        onView(withText(username)).check(isDisplayed)
    }

    fun verifyDrawerEmail(email: String) {
        onView(withText(email)).check(isDisplayed)
    }

    fun verifyDrawerProfileImageIsChanged(context: Context) {
        onView(withId(R.id.base_navbar_header_profile_image))
            .check(matches(not(isDefaultImage(context))))
    }
}
