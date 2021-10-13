package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import org.threeten.bp.Duration

open class DurationSettingsItem(name: Int, context: Context) : SettingsItem<Duration>(
    name,
    context,
    { sharedPreferences, key ->
        Duration.ofMinutes(sharedPreferences.getInt(key, 0).toLong())
    }, { editor, key, duration ->
        val durationMinutes = duration.toMinutes().toInt()
        editor.putInt(key, durationMinutes)
    }
)
