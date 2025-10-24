package net.wojteksz128.worktimemeasureapp.window

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
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
import net.wojteksz128.worktimemeasureapp.util.createTestImageUri
import net.wojteksz128.worktimemeasureapp.util.isDefaultImage
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import org.hamcrest.Matchers.not
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

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    @Inject
    lateinit var dayOffService: DayOffService

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    private lateinit var scenario: ActivityScenario<DashboardActivity>

    @Before
    fun setup() {
        Intents.init()
        hiltRule.inject()

        initialSettingsPreparer.initSettings()

        dayOffService.stub {
            onBlocking { getDayType(any<ZonedDateTime>()) } doReturn DayType.WorkDay
            onBlocking { getDayType(any<LocalDate>()) } doReturn DayType.WorkDay
        }

        scenario = ActivityScenario.launch(DashboardActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
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
        navigateTo(R.id.nav_home)
        onView(withId(R.id.dashboard_content)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToHistory() {
        navigateTo(R.id.nav_history)
        onView(withId(R.id.history_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigationToDaysOffList() {
        navigateTo(R.id.nav_days_off_list)
        composeTestRule.onNodeWithTag("days_off_list_layout").assertIsDisplayed()
    }

    @Test
    fun test_navigationToSettings() {
        navigateTo(R.id.nav_settings)
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
    }

    @Test
    fun test_aboutSnackbar() {
        navigateTo(R.id.nav_about)
        onView(withText(R.string.about)).check(matches(isDisplayed()))
    }

    @Test
    fun whenProfileDataChanged_thenDrawerHeaderIsUpdated() {
        val newUsername = "John Doe"
        val newEmail = "john.doe@example.com"

        // Navigate to Settings
        navigateTo(R.id.nav_settings)

        // Navigate to ProfileFragment
        onView(withText(R.string.settings_header_profile_title)).perform(click())

        // Change username
        onView(withText(R.string.settings_profile_username_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(newUsername))
        onView(withText("OK")).perform(click())

        // Change email
        onView(withText(R.string.settings_profile_email_title)).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(newEmail))
        onView(withText("OK")).perform(click())

        // Go back to DashboardActivity
        pressBack() // Back from ProfileFragment to SettingsActivity
        pressBack() // Back from SettingsActivity to DashboardActivity

        // Open Drawer and check if header is updated
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withText(newUsername)).check(matches(isDisplayed()))
        onView(withText(newEmail)).check(matches(isDisplayed()))
    }

    @Test
    fun whenProfileImageChanged_thenDrawerHeaderIsUpdated() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val testImageUri = createTestImageUri(context)

        val resultData = Intent().setData(testImageUri)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        navigateTo(R.id.nav_settings)
        onView(withText(R.string.settings_header_profile_title)).perform(click())

        onView(withText(R.string.image_view_preference_image_hint)).perform(click())
        intended(hasAction(Intent.ACTION_GET_CONTENT))

        pressBack()
        pressBack()

        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_navbar_header_profile_image))
            .check(matches(not(isDefaultImage(context))))
    }

    private fun navigateTo(navId: Int) {
        onView(withId(R.id.base_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.base_nav_view)).perform(NavigationViewActions.navigateTo(navId))
    }
}
