package net.wojteksz128.worktimemeasureapp.settings

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R
import org.joda.time.Duration

@Suppress("unused")
object Settings {
    object Profile : UnitSettingsItem(R.string.settings_key_profile) {
        val ImagePath = StringSettingsItem(R.string.settings_key_profile_image)
        val Username = StringSettingsItem(R.string.settings_key_profile_username)
        val Email = StringSettingsItem(R.string.settings_key_profile_email)
    }

    // TODO: 27.08.2021 dodaj konfigurację
    object Work : UnitSettingsItem(R.string.settings_key_work) {
        // TODO: 27.08.2021 Napraw - jest to napisane, by mieć jakieś dane bez konfiguracji
        val WorkTimeDuration =
            object : DurationSettingsItem(R.string.settings_key_work_workTimeDuration) {
                override fun getValue(context: Context, defaultValue: Duration?): Duration =
                    Duration.millis(8L * 60 * 60 * 1000 + 30 * 60 * 1000)
            }
    }

    object Messages : UnitSettingsItem(R.string.settings_key_messages) {
        val Signature = StringSettingsItem(R.string.settings_key_messages_signature)
        val ReplyDefaultAction = StringSettingsItem(R.string.settings_key_messages_reply)
    }

    object Sync : UnitSettingsItem(R.string.settings_key_sync) {
        val Sync = BooleanSettingsItem(R.string.settings_key_sync_sync)
        val Attachment = BooleanSettingsItem(R.string.settings_key_sync_attachment)
    }
}

