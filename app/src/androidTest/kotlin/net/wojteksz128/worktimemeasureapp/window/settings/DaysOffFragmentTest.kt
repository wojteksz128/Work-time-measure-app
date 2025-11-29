package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.di.TestRepositoryModule.DUMMY_COUNTRIES
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
import net.wojteksz128.worktimemeasureapp.util.printViewHierarchy
import net.wojteksz128.worktimemeasureapp.util.setSwitchTo
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.timeout
import org.mockito.kotlin.any
import org.mockito.kotlin.verifyBlocking
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DaysOffFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    @Inject
    lateinit var externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade

    private lateinit var context: Context

    @Before
    fun setup() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        initialSettingsPreparer.initSettings()
    }

    private val publicCategoryTitle = withText(R.string.settings_daysOff_public_category_title)
    private val syncWithApiTitle = withText(R.string.settings_daysOff_public_syncWithApi_title)
    private val syncWithApiClickable = allOf(isClickable(), hasDescendant(syncWithApiTitle))
    private val providerTitle = withText(R.string.settings_daysOff_public_provider_title)
    private val countryTitle = withText(R.string.settings_daysOff_public_country_title)
    private val syncNowTitle = withText(R.string.settings_daysOff_public_syncNow_title)
    private val snackbar = withId(com.google.android.material.R.id.snackbar_text)

    // TODO: Two functions below can be merged into one parameterized test
    @Test
    fun givenFragmentStartedWithDisabledSync_thenDisplaysDaysOffPreferences() {
        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(publicCategoryTitle).check(matches(isDisplayed()))
        onView(syncWithApiTitle).check(matches(isDisplayed()))

        onView(syncWithApiClickable).perform(setSwitchTo(false))

        onView(syncWithApiTitle).check(matches(isDisplayed()))
        onView(providerTitle).check(matches(allOf(isDisplayed(), isNotEnabled())))
        onView(countryTitle).check(matches(allOf(isDisplayed(), isNotEnabled())))
        onView(syncNowTitle).check(matches(allOf(isDisplayed(), isNotEnabled())))
    }

    @Test
    fun givenFragmentStartedWithEnabledSync_thenDisplaysDaysOffPreferences() {
        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(publicCategoryTitle).check(matches(isDisplayed()))
        onView(syncWithApiTitle).check(matches(isDisplayed()))

        onView(syncWithApiClickable).perform(setSwitchTo(true))

        onView(syncWithApiTitle).check(matches(isDisplayed()))
        onView(providerTitle).check(matches(allOf(isDisplayed(), isEnabled())))
        onView(countryTitle).check(matches(allOf(isDisplayed(), isEnabled())))
        onView(syncNowTitle).check(matches(allOf(isDisplayed(), isEnabled())))
    }

    @Test
    fun whenSyncWithApiSwitchIsToggled_thenValueIsSaved() {
        val initialValue = Settings.DaysOff.SyncWithAPI.valueNullable ?: false

        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(syncWithApiTitle).perform(click())

        val newValue = Settings.DaysOff.SyncWithAPI.value
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.DaysOff.SyncWithAPI.value)
    }

    @Test
    fun whenHolidayProviderWasChanged_thenValueIsSavedAndListOfCountriesIsUpdated() {
        val initialValue = Settings.DaysOff.Provider.apply {
            value = HolidayProvider.NagerDateAPI
        }.value

        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(syncWithApiClickable).perform(setSwitchTo(true))
        onView(providerTitle).perform(click())
        onView(withText(HolidayProvider.HolidayAPI.displayName)).perform(click())

        val newValue = Settings.DaysOff.Provider.value

        verifyBlocking(
            externalHolidayRepositoriesFacade.forAPI(HolidayProvider.NagerDateAPI),
            timeout(2000).times(1)
        ) { getAvailableCountries() } // Used on load of fragment
        verifyBlocking(
            externalHolidayRepositoriesFacade.forAPI(HolidayProvider.HolidayAPI), timeout(2000)
        ) { getAvailableCountries() }
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.DaysOff.Provider.value)
    }

    @Test
    fun whenCountryWasChanged_thenValueIsSaved() {
        val initialValue = Settings.DaysOff.Country.valueNullable

        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(syncWithApiClickable).perform(setSwitchTo(true))
        onView(providerTitle).perform(click())
        onView(withText(HolidayProvider.HolidayAPI.displayName)).perform(click())
        onView(countryTitle).perform(click())
        onView(withText(DUMMY_COUNTRIES[0].name)).perform(click())

        val newValue = Settings.DaysOff.Country.value
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.DaysOff.Country.value)
    }

    // TODO: Needs to be tested with all implementations of ExternalHolidayRepository
    @Test
    fun whenSyncNowIsClicked_thenCallsGetHolidaysAndShowsSnackbar() {
        launchFragmentInHiltContainer<DaysOffFragment>()

        onView(isRoot()).perform(printViewHierarchy())
        // Sync needs to be enabled to see the sync button
        onView(syncWithApiClickable).perform(setSwitchTo(true))

        // Select an example country
        onView(countryTitle).perform(click())
        onView(withText(DUMMY_COUNTRIES[0].name)).perform(click())

        // Click the sync now button
        onView(syncNowTitle).perform(click())

        // Check that defined in initial settings provider was called
        val holidayRepository =
            externalHolidayRepositoriesFacade.forAPI(Settings.DaysOff.Provider.value)
        verifyBlocking(holidayRepository, timeout(2000)) { getHolidays(any(), any()) }

        // Check for snackbar. The message can be for success or failure.
        onView(snackbar).check(matches(isDisplayed()))
    }
}