package net.wojteksz128.worktimemeasureapp.settings

import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import org.threeten.bp.Duration
import java.util.Calendar.FRIDAY
import java.util.Calendar.MONDAY
import java.util.Calendar.THURSDAY
import java.util.Calendar.TUESDAY
import java.util.Calendar.WEDNESDAY

class InitialSettingsPreparer(
    private val Settings: Settings,
) {

    fun initSettings() {
        if (Settings.Internal.FirstRun.valueNullable != false) {
            prepareSettings()
            Settings.Internal.FirstRun.value = false
        }
    }

    private fun prepareSettings() {
        Settings.WorkTime.NotifyingEnabled.value = true
        Settings.WorkTime.Week.FirstWeekDay.value = MONDAY
        Settings.WorkTime.Week.DaysOfWorkingWeek.value =
            setOf("$MONDAY", "$TUESDAY", "$WEDNESDAY", "$THURSDAY", "$FRIDAY")
        Settings.WorkTime.Week.Duration.value = Duration.ofHours(8)

        Settings.DaysOff.SyncWithAPI.value = false
        Settings.DaysOff.Provider.value = HolidayProvider.NagerDateAPI
        // TODO: Synchronize with external API on first run

        Settings.Sync.TimeSync.Enabled.value = false
        Settings.Sync.TimeSync.ServerAddress.value = null

        Settings.Internal.AlarmState.value = TimerManager.AlarmState.NotSet
        Settings.Internal.FirstRun.value = false
    }
}
