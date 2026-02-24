package net.wojteksz128.worktimemeasureapp.window.settings

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.di.TestRepositoryModule.DUMMY_COUNTRIES
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.launchFragmentInHiltContainer
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
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
        initialSettingsPreparer.initSettings()
        launchFragmentInHiltContainer<DaysOffFragment>()
    }

    @Test
    fun givenFragmentStarted_thenDisplaysDaysOffPreferences() {
        daysOffSettings {
            verifyIsDisplayed()
        }
    }

    @Test
    fun givenFragmentStartedWithDisabledSync_thenElementsAreDisabled() {
        daysOffSettings {
            setSyncWithApi(false)
            verifySyncDisabled()
        }
    }

    @Test
    fun givenFragmentStartedWithEnabledSync_thenElementsAreEnabled() {
        daysOffSettings {
            setSyncWithApi(true)
            verifySyncEnabledButNotFulfilled()
        }
    }

    @Test
    fun whenSyncWithApiSwitchIsToggled_thenValueIsSaved() {
        val initialValue = Settings.DaysOff.SyncWithAPI.valueNullable ?: false

        daysOffSettings {
            toggleSyncWithApi()
        }

        val newValue = Settings.DaysOff.SyncWithAPI.value
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.DaysOff.SyncWithAPI.value)
    }

    @Test
    fun whenHolidayProviderWasChanged_thenValueIsSavedAndListOfCountriesIsUpdated() {
        val initialValue = Settings.DaysOff.Provider.value

        daysOffSettings {
            setSyncWithApi(true)
            changeProvider(HolidayProvider.HolidayAPI)
        }

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

        daysOffSettings {
            setSyncWithApi(true)
            changeProvider(HolidayProvider.HolidayAPI)
            changeCountry(DUMMY_COUNTRIES[0].name)
        }

        val newValue = Settings.DaysOff.Country.value
        assertNotEquals(initialValue, newValue)
        assertEquals(newValue, Settings.DaysOff.Country.value)
    }

    @Test
    fun whenCountryWasSelected_thenSyncNowIsAvailable() {
        daysOffSettings {
            setSyncWithApi(true)
            changeProvider(HolidayProvider.HolidayAPI)
            changeCountry(DUMMY_COUNTRIES[0].name)
            verifySyncEnabledAndFulfilled()
        }
    }

    // TODO: Needs to be tested with all implementations of ExternalHolidayRepository
    @Test
    fun whenSyncNowIsClicked_thenCallsGetHolidaysAndShowsSnackbar() {
        daysOffSettings {
            setSyncWithApi(true)
            changeCountry(DUMMY_COUNTRIES[0].name)
            syncNow()
        }

        val holidayRepository =
            externalHolidayRepositoriesFacade.forAPI(Settings.DaysOff.Provider.value)
        verifyBlocking(holidayRepository, timeout(2000)) { getHolidays(any(), any()) }

        daysOffSettings {
            verifySnackbarIsDisplayed()
        }
    }
}
