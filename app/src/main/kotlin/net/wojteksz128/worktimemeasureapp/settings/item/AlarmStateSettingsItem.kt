package net.wojteksz128.worktimemeasureapp.settings.item

import net.wojteksz128.worktimemeasureapp.util.TimerManager

class AlarmStateSettingsItem(name: Int) : SettingsItem<TimerManager.AlarmState>(
    name,
    { sharedPreferences, key -> TimerManager.AlarmState.values()[sharedPreferences.getInt(key, 0)] },
    {editor, key, value -> editor.putInt(key, value.ordinal) }
)
