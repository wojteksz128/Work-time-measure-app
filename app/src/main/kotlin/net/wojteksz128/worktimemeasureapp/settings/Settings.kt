package net.wojteksz128.worktimemeasureapp.settings

import android.content.Context
import org.joda.time.Duration

@Suppress("unused")
object Settings {
    object Profile : UnitSettingsItem("profile") {
        val ImagePath = StringSettingsItem("settings_profile_image")
        val Username = StringSettingsItem("settings_profile_username")
        val Email = StringSettingsItem("settings_profile_email")
    }

    // TODO: 27.08.2021 dodaj konfigurację
    object Work : UnitSettingsItem("work") {
        // TODO: 27.08.2021 Napraw - jest to napisane, by mieć jakieś dane bez konfiguracji
        val WorkTimeDuration = object : DurationSettingsItem("settings_work_workTimeDuration") {
            override fun getValue(context: Context, defaultValue: Duration?): Duration =
                Duration.millis(8L * 60 * 60 * 1000 + 30 * 60 * 1000)
        }
    }

    object Messages : UnitSettingsItem("messages") {
        val Signature = StringSettingsItem("signature")
        val ReplyDefaultAction = StringSettingsItem("reply")
    }

    object Sync : UnitSettingsItem("sync") {
        val Sync = BooleanSettingsItem("sync")
        val Attachment = BooleanSettingsItem("attachment")
    }
}

