package net.wojteksz128.worktimemeasureapp.settings.converter

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

class ConfigurationConverterFrom1To2 @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : VersionedConfigurationConverter, ClassTagAware {
    override val fromVersion: Int
        get() = 1
    override val toVersion: Int
        get() = 2

    override fun convert(settings: Settings) {
        Log.d(classTag, "Start converting from $fromVersion to $toVersion")

        PreferenceManager.getDefaultSharedPreferences(appContext).edit {
            remove("settings_internal_alarmSetTime")
        }
        Log.d(classTag, "Finished converting from $fromVersion to $toVersion")
    }
}