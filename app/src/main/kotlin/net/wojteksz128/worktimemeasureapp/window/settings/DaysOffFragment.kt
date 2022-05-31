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
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import javax.inject.Inject

@AndroidEntryPoint
class DaysOffFragment : BasePreferenceFragment(R.xml.days_off_preferences) {

    @Inject
    lateinit var externalHolidayRepositoriesFacade: ExternalHolidayRepositoriesFacade

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    @Inject
    lateinit var dayOffRepository: DayOffRepository

    @Suppress("PropertyName")
    @Inject
    lateinit var Settings: Settings

    override fun onPreferencesInit() {
        initCountriesList()
        initSyncNowButton()
    }

    private fun initCountriesList() {
        val countriesPreference =
            findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_country))!!
        lifecycleScope.launch {
            // TODO: Dodaj wybierajkę informacji o zewnętrznym źródle informacji o świętach.
            try {
                val countries =
                    externalHolidayRepositoriesFacade.forAPI(HolidayProvider.NagerDateAPI)
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
                    // TODO: Dodaj wybierajkę informacji o zewnętrznym źródle informacji o świętach.
                    externalHolidayRepositoriesFacade.forAPI(HolidayProvider.NagerDateAPI)
                        .getHolidays().forEach {
                            withContext(Dispatchers.IO) {
                                dayOffRepository.save(it)
                            }
                        }
                    // TODO: Dodaj wybierajkę informacji o zewnętrznym źródle informacji o świętach.
                    Snackbar.make(requireContext(),
                        view!!,
                        "Holidays fetched from ${HolidayProvider.NagerDateAPI.displayName}.",
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