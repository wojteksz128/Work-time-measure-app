package net.wojteksz128.worktimemeasureapp.settings

import net.wojteksz128.worktimemeasureapp.settings.item.*

class Settings(
    val Profile: ProfileSettings,
    val WorkTime: WorkTimeSettings,
    val Sync: SyncSettings,
    val Internal: InternalSettings
) : SettingsItemsAware(), SettingsNode {
    override val childNodes: Set<SettingsItem<*>>
        get() = generateChildren(Profile, WorkTime, Sync, Internal)

    class ProfileSettings (
        val ImagePath: StringSettingsItem,
        val Username: StringSettingsItem,
        val Email: StringSettingsItem
    ) : SettingsNode {
        override val childNodes: Set<SettingsItem<*>>
            get() = generateChildren(ImagePath, Username, Email)
    }

    class WorkTimeSettings (
        val NotifyingEnabled: BooleanSettingsItem,
        val Week: WeekSettings
    ) : SettingsNode {
        override val childNodes: Set<SettingsItem<*>>
            get() = generateChildren(NotifyingEnabled, Week)

        class WeekSettings(
            val FirstWeekDay: IntFromStringSettingsItem,
            val DaysOfWorkingWeek: StringsArraySettingsItem,
            val Duration: DurationSettingsItem
        ) : SettingsNode {
            override val childNodes: Set<SettingsItem<*>>
                get() = generateChildren(FirstWeekDay, DaysOfWorkingWeek, Duration)
        }
    }

    class SyncSettings (val TimeSync: TimeSyncSettings) : SettingsNode {
        override val childNodes: Set<SettingsItem<*>>
            get() = generateChildren(TimeSync)

        class TimeSyncSettings (
            val Enabled: BooleanSettingsItem,
            val ServerAddress: StringSettingsItem
        ) : SettingsNode {
            override val childNodes: Set<SettingsItem<*>>
                get() = generateChildren(Enabled, ServerAddress)
        }
    }

    class InternalSettings (
        val AlarmState: AlarmStateSettingsItem
    ) : SettingsNode {
        override val childNodes: Set<SettingsItem<*>>
            get() = generateChildren(AlarmState)
    }
}

private fun generateChildren(vararg node: SettingsNode): Set<SettingsItem<*>> =
    node.flatMap { it.childNodes }.toSet()

