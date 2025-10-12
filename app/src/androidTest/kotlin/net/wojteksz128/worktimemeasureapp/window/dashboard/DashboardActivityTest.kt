package net.wojteksz128.worktimemeasureapp.window.dashboard

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
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayType
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DashboardActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dayOffService: DayOffService

    private lateinit var scenario: ActivityScenario<DashboardActivity>

    @Before
    fun setup() {
        hiltRule.inject()

        runBlocking {
            // Konfiguracja mocka PRZED uruchomieniem aktywności jest kluczowa
            doAnswer { DayType.WorkDay }.whenever(dayOffService).getDayType(any<ZonedDateTime>())
            doAnswer { DayType.WorkDay }.whenever(dayOffService).getDayType(any<LocalDate>())
        }

        // Ręczne uruchomienie aktywności PO skonfigurowaniu mocków
        scenario = ActivityScenario.launch(DashboardActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun test_activityInView() {
        // Sprawdza, czy layout dashboardu jest widoczny
        onView(withId(R.id.dashboard_content)).check(matches(isDisplayed()))
    }

    @Test
    fun test_fabIsDisplayedAndClickable() {
        // Sprawdza, czy FAB jest widoczny i klikalny
        onView(withId(R.id.dashboard_enter_fab)).check(matches(isDisplayed()))
        onView(withId(R.id.dashboard_enter_fab)).perform(click())
        // TODO: Dodać weryfikację startu serwisu i odliczania czasu, gdy podasz ID pól
    }

    @Test
    fun test_navigationDrawer() {
        // Otwiera szufladę nawigacji i sprawdza, czy jest widoczna
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToHistory() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history))
        // Weryfikuje, czy layout HistoryActivity jest widoczny
        onView(withId(R.id.history_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToSettings() {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings))
        // Weryfikuje, czy layout SettingsActivity jest widoczny
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
    }
}
