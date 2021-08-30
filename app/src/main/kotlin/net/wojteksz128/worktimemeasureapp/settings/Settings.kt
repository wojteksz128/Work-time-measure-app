package net.wojteksz128.worktimemeasureapp.settings

import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.item.*

object Settings : SettingsItemsAware() {
    object Profile {
        val ImagePath = StringSettingsItem(R.string.settings_key_profile_image)
        val Username = StringSettingsItem(R.string.settings_key_profile_username)
        val Email = StringSettingsItem(R.string.settings_key_profile_email)
    }

    object WorkTime {
        val NotifyingEnabled = BooleanSettingsItem(R.string.settings_key_workTime_notify_enable)
        val Duration = DurationSettingsItem(R.string.settings_key_workTime_duration)
        val FirstWeekDay = IntFromStringSettingsItem(R.string.settings_key_workTime_firstWeekDay)
    }

    object Sync {
        object TimeSync {
            val Enabled = BooleanSettingsItem(R.string.settings_key_sync_timeSync_enable)
            val ServerAddress = StringSettingsItem(R.string.settings_key_sync_timeSync_server)
        }
    }
}

