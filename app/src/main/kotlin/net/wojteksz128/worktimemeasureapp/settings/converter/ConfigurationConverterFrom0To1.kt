package net.wojteksz128.worktimemeasureapp.settings.converter

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.threeten.bp.DayOfWeek
import java.util.Calendar
import javax.inject.Inject

class ConfigurationConverterFrom0To1 @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : VersionedConfigurationConverter, ClassTagAware {
    override val fromVersion: Int = 0
    override val toVersion: Int = 1

    override fun convert(settings: Settings) {
        Log.d(classTag, "Start converting from $fromVersion to $toVersion")

        convertDayOfWeekAsIntToString(settings)
        convertDaysOfWorkingWeek(settings)
        settings.Internal.ConfigurationVersion.value = toVersion

        Log.d(classTag, "Finished converting from $fromVersion to $toVersion")
    }

    private fun convertDayOfWeekAsIntToString(settings: Settings) {
        Log.d(classTag, "convertDayOfWeekAsIntToString: Converting DayOfWeek as Int to String")

        val firstWeekDayKey = settings.WorkTime.Week.FirstWeekDay.key
        val firstWeekDayOldValue = PreferenceManager.getDefaultSharedPreferences(appContext)
            .getString(firstWeekDayKey, null)?.toInt()

        Log.d(classTag, "convertDayOfWeekAsIntToString: old value: $firstWeekDayOldValue")

        firstWeekDayOldValue?.let {
            settings.WorkTime.Week.FirstWeekDay.value = convertCalendarDayOfWeekToString(it)
        }

        Log.d(
            classTag,
            "convertDayOfWeekAsIntToString: new value: ${settings.WorkTime.Week.FirstWeekDay.value}"
        )
    }

    private fun convertDaysOfWorkingWeek(settings: Settings) {
        Log.d(classTag, "convertDaysOfWorkingWeek: Converting days of working week")

        val daysOfWorkingWeekKey = settings.WorkTime.Week.DaysOfWorkingWeek.key
        val daysOfWorkingWeekOldValue = PreferenceManager.getDefaultSharedPreferences(appContext)
            .getStringSet(daysOfWorkingWeekKey, emptySet())

        Log.d(classTag, "convertDaysOfWorkingWeek: old value: $daysOfWorkingWeekOldValue")

        settings.WorkTime.Week.DaysOfWorkingWeek.value =
            daysOfWorkingWeekOldValue?.map { convertCalendarDayOfWeekToString(it.toInt()) }?.toSet()
                ?: emptySet()

        Log.d(
            classTag,
            "convertDaysOfWorkingWeek: new value: ${settings.WorkTime.Week.DaysOfWorkingWeek.value}"
        )
    }

    private fun convertCalendarDayOfWeekToString(dayOfWeek: Int): String = when (dayOfWeek) {
        Calendar.MONDAY -> DayOfWeek.MONDAY.name
        Calendar.TUESDAY -> DayOfWeek.TUESDAY.name
        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY.name
        Calendar.THURSDAY -> DayOfWeek.THURSDAY.name
        Calendar.FRIDAY -> DayOfWeek.FRIDAY.name
        Calendar.SATURDAY -> DayOfWeek.SATURDAY.name
        Calendar.SUNDAY -> DayOfWeek.SUNDAY.name
        else -> throw IllegalArgumentException("Unknown day of week: $dayOfWeek")
    }
}