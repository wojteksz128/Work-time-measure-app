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
import net.wojteksz128.worktimemeasureapp.window.settings.property.AsyncActionPreference
import net.wojteksz128.worktimemeasureapp.window.settings.property.AsyncActionPreference.Listener
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
        findPreference<AsyncActionPreference>(getString(R.string.settings_key_daysOff_public_syncNow))!!.apply {
            listener = object : Listener {
                override suspend fun onAsyncClick() {
                    val message = try {
                        externalHolidayRepositoriesFacade.forAPI(Settings.DaysOff.Provider.value)
                            .getHolidays().forEach {
                                withContext(Dispatchers.IO) {
                                    dayOffRepository.save(it)
                                }
                            }
                        "Holidays fetched from ${Settings.DaysOff.Provider.value.displayName}."
                    } catch (e: ApiErrorResponse) {
                        e.message!!
                    }

                    Snackbar.make(requireContext(), view!!, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}