package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import net.wojteksz128.worktimemeasureapp.util.TimerManager

class AlarmStateSettingsItem(name: Int, context: Context) : SettingsItem<TimerManager.AlarmState>(
    name,
    context,
    { sharedPreferences, key -> TimerManager.AlarmState.values()[sharedPreferences.getInt(key, 0)] },
    {editor, key, value -> editor.putInt(key, value.ordinal) }
)
