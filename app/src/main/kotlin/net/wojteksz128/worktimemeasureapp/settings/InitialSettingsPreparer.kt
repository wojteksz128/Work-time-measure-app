package net.wojteksz128.worktimemeasureapp.settings

import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.settings.converter.ConfigurationConverterFactory
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration

class InitialSettingsPreparer(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    private val configurationVersion: String,
    private val configurationConverterFactory: ConfigurationConverterFactory,
) {

    fun initSettings() {
        if (Settings.Internal.FirstRun.valueNullable != false) {
            prepareSettings()
            Settings.Internal.FirstRun.value = false
        } else if (Settings.Internal.ConfigurationVersion.valueNullable != configurationVersion.toInt()) {
            val configurationConverter = configurationConverterFactory.create(
                Settings.Internal.ConfigurationVersion.valueNullable,
                configurationVersion.toInt()
            )
            configurationConverter.convert(Settings)
        }
    }

    private fun prepareSettings() {
        Settings.WorkTime.NotifyingEnabled.value = true
        Settings.WorkTime.Week.FirstWeekDay.value = DayOfWeek.MONDAY.name
        Settings.WorkTime.Week.DaysOfWorkingWeek.value =
            setOf(
                DayOfWeek.MONDAY.name,
                DayOfWeek.TUESDAY.name,
                DayOfWeek.WEDNESDAY.name,
                DayOfWeek.THURSDAY.name,
                DayOfWeek.FRIDAY.name
            )
        Settings.WorkTime.Week.Duration.value = Duration.ofHours(8)

        Settings.DaysOff.SyncWithAPI.value = false
        Settings.DaysOff.Provider.value = HolidayProvider.NagerDateAPI
        // TODO: Synchronize with external API on first run

        Settings.Sync.TimeSync.Enabled.value = false
        Settings.Sync.TimeSync.ServerAddress.value = null

        Settings.Internal.AlarmState.value = TimerManager.AlarmState.NotSet
        Settings.Internal.FirstRun.value = false
        Settings.Internal.ConfigurationVersion.value = configurationVersion.toInt()
    }
}
