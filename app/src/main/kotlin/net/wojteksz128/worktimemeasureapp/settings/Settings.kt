package net.wojteksz128.worktimemeasureapp.settings

import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.settings.item.*

class Settings(
    val Profile: ProfileSettings,
    val WorkTime: WorkTimeSettings,
    val DaysOff: DaysOffSettings,
    val Sync: SyncSettings,
    val Internal: InternalSettings,
) : SettingsItemsAware(Profile, WorkTime, DaysOff, Sync, Internal) {

    class ProfileSettings(
        val ImagePath: StringSettingsItem,
        val Username: StringSettingsItem,
        val Email: StringSettingsItem,
    ) : SettingsNode(ImagePath, Username, Email)

    class WorkTimeSettings(
        val NotifyingEnabled: BooleanSettingsItem,
        val Week: WeekSettings,
    ) : SettingsNode(NotifyingEnabled, Week) {

        class WeekSettings(
            val FirstWeekDay: IntFromStringSettingsItem,
            val DaysOfWorkingWeek: StringsArraySettingsItem,
            val Duration: DurationSettingsItem,
        ) : SettingsNode(FirstWeekDay, DaysOfWorkingWeek, Duration)
    }

    class DaysOffSettings(
        val SyncWithAPI: BooleanSettingsItem,
        val Provider: EnumSettingsItem<HolidayProvider>,
        val Country: StringSettingsItem,
        /* Sync Now is not added - it is only button, not store value */
    ) : SettingsNode(SyncWithAPI, Provider, Country)

    class SyncSettings(val TimeSync: TimeSyncSettings) : SettingsNode(TimeSync) {

        class TimeSyncSettings(
            val Enabled: BooleanSettingsItem,
            val ServerAddress: StringSettingsItem,
        ) : SettingsNode(Enabled, ServerAddress)
    }

    class InternalSettings(
        val AlarmState: AlarmStateSettingsItem,
        val FirstRun: BooleanSettingsItem,
    ) : SettingsNode(AlarmState, FirstRun)
}

