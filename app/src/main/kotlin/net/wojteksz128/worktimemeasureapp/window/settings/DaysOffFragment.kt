package net.wojteksz128.worktimemeasureapp.window.settings

import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.holidayapi.HolidayApiService
import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffSource
import net.wojteksz128.worktimemeasureapp.model.fieldType.DayOffType
import net.wojteksz128.worktimemeasureapp.repository.DayOffRepository
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import org.threeten.bp.Month
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DaysOffFragment : BasePreferenceFragment(R.xml.days_off_preferences) {

    @Inject
    lateinit var holidayApiService: HolidayApiService

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

    private fun initSyncNowButton() {
        val syncNow =
            findPreference<Preference>(getString(R.string.settings_key_daysOff_public_syncNow))!!
        syncNow.setOnPreferenceClickListener {
            lifecycleScope.launch {
                val holidays = holidayApiService.getHolidays(
                    Settings.DaysOff.Country.value,
                    dateTimeProvider.currentCalendar.get(Calendar.YEAR),
                    public = true,
                    language = Locale.getDefault().language
                )
                when (holidays.isSuccessful) {
                    true -> {
                        holidays.body()!!.holidays.map {
                            val dayOffCalendar = Calendar.getInstance().apply { time = it.date }
                            val dayOffDay = dayOffCalendar.get(Calendar.DAY_OF_MONTH)
                            val dayOffMonth = Month.of(dayOffCalendar.get(Calendar.MONTH) + 1)
                            val dayOffYear = dayOffCalendar.get(Calendar.YEAR)
                            val dayOff = DayOff(
                                null,
                                it.uuid,
                                DayOffType.PublicHoliday,
                                it.name,
                                dayOffDay,
                                dayOffMonth,
                                dayOffYear,
                                dayOffDay,
                                dayOffMonth,
                                dayOffYear,
                                DayOffSource.ExternalAPI
                            )
                            withContext(Dispatchers.IO) {
                                dayOffRepository.save(dayOff)
                            }
                        }
                    }
                    else -> {}
                }
            }
            false
        }
    }
}