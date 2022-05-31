package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.repository.api.ApiErrorResponse
import net.wojteksz128.worktimemeasureapp.repository.api.ExternalHolidayRepositoriesFacade
import net.wojteksz128.worktimemeasureapp.settings.Settings
import javax.inject.Inject

@AndroidEntryPoint
class DaysOffFragment : BasePreferenceFragment(R.xml.days_off_preferences) {

    @Inject
    lateinit var externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade

    @Inject
    lateinit var dayOffRepository: DayOffRepository

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    override fun onPreferencesInit() {
        initHolidayProviderList()
        initCountriesList()
        initSyncNowButton()
    }

    private fun initHolidayProviderList() {
        findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_provider))?.apply {
            entryValues = HolidayProvider.values().map { it.name }.toTypedArray()
            entries = HolidayProvider.values().map { it.displayName }.toTypedArray()
            setDefaultValue(HolidayProvider.NagerDateAPI.name)
            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, _ ->
                    initCountriesList()
                    false
                }
        }
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
            } catch (e: ApiErrorResponse) {
                Snackbar.make(requireContext(), view!!, e.message!!, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun initSyncNowButton() {
        val syncNow =
            findPreference<Preference>(getString(R.string.settings_key_daysOff_public_syncNow))!!
        syncNow.setOnPreferenceClickListener {
            lifecycleScope.launch {
                try {
                    externalHolidayRepositoriesFacade.forAPI(Settings.DaysOff.Provider.value)
                        .getHolidays().forEach {
                            withContext(Dispatchers.IO) {
                                dayOffRepository.save(it)
                            }
                        }
                    Snackbar.make(requireContext(),
                        view!!,
                        "Holidays fetched from ${Settings.DaysOff.Provider.value.displayName}.",
                        Snackbar.LENGTH_LONG)
                        .show()
                } catch (e: ApiErrorResponse) {
                    Snackbar.make(requireContext(), view!!, e.message!!, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
            false
        }
    }
}