package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
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

    override fun onPreferencesInit() {
        onChangeHolidayProvider(Settings.DaysOff.Provider.value)
        initHolidayProviderList()
        initSyncNowButton()
    }

    private fun initHolidayProviderList() {
        findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_provider))?.apply {
            entryValues = HolidayProvider.values().map { it.name }.toTypedArray()
            entries = HolidayProvider.values().map { it.displayName }.toTypedArray()
            setDefaultValue(HolidayProvider.NagerDateAPI.name)
            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val holidayProvider = HolidayProvider.valueOf(newValue as String)
                    onChangeHolidayProvider(holidayProvider)
                    true
                }
        }
    }

    private fun onChangeHolidayProvider(newHolidayProvider: HolidayProvider) {
        changeSyncWithApiSwitchSummaryProvider(newHolidayProvider)
        initCountriesList()
    }

    private fun changeSyncWithApiSwitchSummaryProvider(holidayProvider: HolidayProvider) {
        findPreference<SwitchPreferenceCompat>(getString(R.string.settings_key_daysOff_public_syncWithApi))?.summaryProvider =
            SyncWithAPISwitchSummaryProvider(holidayProvider)
    }

    private fun initCountriesList() {
        val countriesPreference =
            findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_country))!!
        lifecycleScope.launch {
            try {
                val countries =
                    externalHolidayRepositoriesFacade.forAPI(Settings.DaysOff.Provider.value)
                        .getAvailableCountries()
                countriesPreference.entryValues = countries.map { it.code }.toTypedArray()
                countriesPreference.entries = countries.map { it.name }.toTypedArray()
                countriesPreference.summaryProvider = countriesPreference.summaryProvider
            } catch (e: ApiErrorResponse) {
                Snackbar.make(requireContext(), requireView(), e.message!!, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initSyncNowButton() {
        findPreference<AsyncActionPreference>(getString(R.string.settings_key_daysOff_public_syncNow))!!.apply {
            listener = object : Listener {
                override suspend fun onAsyncClick() {
                    val message = try {
                        val holidayProvider = Settings.DaysOff.Provider.value
                        withContext(Dispatchers.IO) {
                            daysOffService.syncHolidaysWith(holidayProvider)
                        }
                        getString(
                            R.string.settings_daysOff_public_syncNow_success_message,
                            holidayProvider.displayName
                        )
                    } catch (e: ApiErrorResponse) {
                        getString(R.string.settings_daysOff_public_syncNow_fail_message,
                            e.message!!)
                    }

                    Snackbar.make(requireContext(), view!!, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private inner class SyncWithAPISwitchSummaryProvider(val holidayProvider: HolidayProvider) :
        Preference.SummaryProvider<SwitchPreferenceCompat> {

        override fun provideSummary(preference: SwitchPreferenceCompat): CharSequence {
            return if (preference.isChecked) getString(R.string.settings_daysOff_public_syncWithApi_summary_on,
                holidayProvider.displayName) else getString(R.string.settings_daysOff_public_syncWithApi_summary_off)
        }
    }
}