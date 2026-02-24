package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.module.dayOff.DayOffService
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ApiErrorResponse
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.window.settings.property.AsyncActionPreference
import net.wojteksz128.worktimemeasureapp.window.settings.property.AsyncActionPreference.Listener
import javax.inject.Inject

@AndroidEntryPoint
class DaysOffFragment : BasePreferenceFragment(R.xml.days_off_preferences), ClassTagAware {

    @Inject
    lateinit var externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade

    @Inject
    lateinit var dayOffRepository: DayOffRepository

    @Inject
    lateinit var daysOffService: DayOffService

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    private lateinit var syncWithApiPreference: SwitchPreferenceCompat

    private lateinit var providerPreference: ListPreference

    private lateinit var countryPreference: ListPreference

    private lateinit var syncNowPreference: AsyncActionPreference

    override fun onPreferencesInit() {
        val holidayProvider = Settings.DaysOff.Provider.value

        syncWithApiPreference =
            findPreference<SwitchPreferenceCompat>(getString(R.string.settings_key_daysOff_public_syncWithApi))!!.apply {
                this.summaryProvider = SyncWithAPISwitchSummaryProvider(holidayProvider)
                this.onPreferenceChangeListener = SyncWithApiChangeListener()
            }

        providerPreference =
            findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_provider))!!.apply {
                this.entryValues = HolidayProvider.entries.map { it.name }.toTypedArray<String>()
                this.entries = HolidayProvider.entries.map { it.displayName }.toTypedArray<String>()
                this.onPreferenceChangeListener = ProviderChangeListener()
            }

        countryPreference =
            findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_country))!!.apply {
                this.onPreferenceChangeListener = CountryChangeListener()
            }

        syncNowPreference =
            findPreference<AsyncActionPreference>(getString(R.string.settings_key_daysOff_public_syncNow))!!.apply {
                this.listener = SyncNowClickListener()
            }

        onChangeHolidayProvider(holidayProvider)
    }

    private fun onChangeHolidayProvider(newHolidayProvider: HolidayProvider) {
        (syncWithApiPreference.summaryProvider as SyncWithAPISwitchSummaryProvider).holidayProvider =
            newHolidayProvider
        fillCountriesList(newHolidayProvider)
    }

    private fun fillCountriesList(newHolidayProvider: HolidayProvider) {
        lifecycleScope.launch {
            try {
                val countries =
                    externalHolidayRepositoriesFacade.forAPI(newHolidayProvider)
                        .getAvailableCountries()
                countryPreference.entryValues = countries.map { it.code }.toTypedArray()
                countryPreference.entries = countries.map { it.name }.toTypedArray()
                val currentlySelectedCountry = countryPreference.value
                countryPreference.value = null
                if (currentlySelectedCountry.isNullOrEmpty() || countryPreference.entryValues.none { it == currentlySelectedCountry }) {
                    countryPreference.value = null
                    countryPreference.callChangeListener(null)
                } else {
                    countryPreference.value = currentlySelectedCountry
                    countryPreference.callChangeListener(currentlySelectedCountry)
                }
            } catch (e: ApiErrorResponse) {
                Snackbar.make(requireContext(), requireView(), e.message!!, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private inner class SyncWithAPISwitchSummaryProvider(var holidayProvider: HolidayProvider) :
        Preference.SummaryProvider<SwitchPreferenceCompat> {

        override fun provideSummary(preference: SwitchPreferenceCompat): CharSequence {
            return if (preference.isChecked) getString(R.string.settings_daysOff_public_syncWithApi_summary_on,
                holidayProvider.displayName) else getString(R.string.settings_daysOff_public_syncWithApi_summary_off)
        }
    }

    private inner class SyncWithApiChangeListener : OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            val isSyncEnabled = newValue as Boolean
            val hasCountrySelected = !countryPreference.value.isNullOrEmpty()
            syncNowPreference.isEnabled = isSyncEnabled && hasCountrySelected
            return true
        }
    }

    private inner class ProviderChangeListener : OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            val holidayProvider = HolidayProvider.valueOf(newValue as String)
            onChangeHolidayProvider(holidayProvider)
            return true
        }
    }

    private inner class CountryChangeListener : OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            val isSyncEnabled = syncWithApiPreference.isChecked
            val hasCountrySelected = !(newValue as String?).isNullOrEmpty()
            syncNowPreference.isEnabled = isSyncEnabled && hasCountrySelected
            return true
        }
    }

    private inner class SyncNowClickListener : Listener {
        override suspend fun onAsyncClick() {
            val holidayProvider = Settings.DaysOff.Provider.value

            val message = try {
                withContext(Dispatchers.IO) {
                    daysOffService.syncHolidaysWith(holidayProvider)
                }
                getString(
                    R.string.settings_daysOff_public_syncNow_success_message,
                    holidayProvider.displayName
                )
            } catch (e: ApiErrorResponse) {
                getString(
                    R.string.settings_daysOff_public_syncNow_fail_message,
                    e.message!!
                )
            }

            Snackbar.make(requireContext(), view!!, message, Snackbar.LENGTH_LONG)
                .show()
        }
    }
}