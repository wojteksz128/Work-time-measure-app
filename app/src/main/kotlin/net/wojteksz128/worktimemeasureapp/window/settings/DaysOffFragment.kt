package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import javax.inject.Inject

@AndroidEntryPoint
class DaysOffFragment : BasePreferenceFragment(R.xml.days_off_preferences) {

    @Inject
    lateinit var holidayApiService: HolidayApiService

    override fun onPreferencesInit() {
        val countries =
            findPreference<ListPreference>(getString(R.string.settings_key_daysOff_public_country))!!
        lifecycleScope.launch {
            val countries1 = holidayApiService.getCountries()
            when (countries1.isSuccessful) {
                true -> {
                    val countriesResponse = countries1.body()!!
                    val map = countriesResponse.countries
                        .sortedBy { it.name }
                        .map { Pair(it.code, it.name) }
                    countries.entryValues = map.map { it.first }.toTypedArray()
                    countries.entries = map.map { it.second }.toTypedArray()
                }
                else -> {}
            }
        }
    }
}