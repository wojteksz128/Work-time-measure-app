package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import net.wojteksz128.worktimemeasureapp.util.setSwitchTo
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SyncFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val timeSyncCategoryTitle = withText(R.string.settings_sync_timeSync_category_title)
    private val enableTitle = withText(R.string.settings_sync_timeSync_enable_title)
    private val serverTitle = withText(R.string.settings_sync_timeSync_server_title)
    private val enableClickable = allOf(isClickable(), hasDescendant(enableTitle))

    @Test
    fun givenFragmentStartedWithDisabledSync_thenDisplaysSyncPreferences() {
        launchFragmentInHiltContainer<SyncFragment>()

        onView(timeSyncCategoryTitle).check(matches(isDisplayed()))

        onView(enableClickable).perform(setSwitchTo(false))

        onView(enableTitle).check(matches(isDisplayed()))
        onView(serverTitle).check(matches(allOf(isDisplayed(), isNotEnabled())))
    }

    @Test
    fun givenFragmentStartedWithEnabledSync_thenDisplaysSyncPreferences() {
        launchFragmentInHiltContainer<SyncFragment>()

        onView(timeSyncCategoryTitle).check(matches(isDisplayed()))

        onView(enableClickable).perform(setSwitchTo(true))

        onView(enableTitle).check(matches(isDisplayed()))
        onView(serverTitle).check(matches(allOf(isDisplayed(), isEnabled())))
    }

    @Test
    fun whenTimeSyncSwitchIsToggled_thenValueIsSaved() {
        val initialValue = Settings.Sync.TimeSync.Enabled.valueNullable ?: false

        launchFragmentInHiltContainer<SyncFragment>()

        onView(enableTitle).perform(click())

        val newValue = Settings.Sync.TimeSync.Enabled.value
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.Sync.TimeSync.Enabled.value)
    }

    @Test
    fun whenTimeSyncServerIsChanged_thenValueIsSaved() {
        val initialValue =
            runBlocking { Settings.Sync.TimeSync.ServerAddress.getValueAsync() ?: "" }
        val newServerAddress = "time.google.com"

        launchFragmentInHiltContainer<SyncFragment>()

        onView(enableTitle).perform(click())

        onView(serverTitle).perform(click())
        onView(withId(android.R.id.edit)).perform(replaceText(newServerAddress))
        Thread.sleep(50)
        onView(withText("OK")).perform(click())

        val newValue = runBlocking { Settings.Sync.TimeSync.ServerAddress.getValueAsync() }
        assertEquals(newServerAddress, newValue?.hostName)
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, runBlocking { Settings.Sync.TimeSync.ServerAddress.getValueAsync() })

    }
}