package net.wojteksz128.worktimemeasureapp.settings

import net.wojteksz128.worktimemeasureapp.R

@Suppress("unused")
object Settings {
    object Profile : UnitSettingsItem(R.string.settings_key_profile) {
        val ImagePath = StringSettingsItem(R.string.settings_key_profile_image)
        val Username = StringSettingsItem(R.string.settings_key_profile_username)
        val Email = StringSettingsItem(R.string.settings_key_profile_email)
    }

    object WorkTime : UnitSettingsItem(R.string.settings_key_workTime) {
        val NotifyingEnabled = BooleanSettingsItem(R.string.settings_key_workTime_notify_enable)
        val Duration = DurationSettingsItem(R.string.settings_key_workTime_duration)
    }

    object Messages : UnitSettingsItem(R.string.settings_key_messages) {
        val Signature = StringSettingsItem(R.string.settings_key_messages_signature)
        val ReplyDefaultAction = StringSettingsItem(R.string.settings_key_messages_reply)
    }

    object Sync : UnitSettingsItem(R.string.settings_key_sync) {
        object TimeSync {
            val Enabled = BooleanSettingsItem(R.string.settings_key_sync_timeSync_enable)
            val ServerAddress = StringSettingsItem(R.string.settings_key_sync_timeSync_server)
        }
    }
}

