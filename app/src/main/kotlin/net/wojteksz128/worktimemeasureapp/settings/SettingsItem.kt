package net.wojteksz128.worktimemeasureapp.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

open class SettingsItem<R>(
    private val keyResourceId: Int,
    private val valueGettingMethod: (SharedPreferences, String, R?) -> R?
) {

    open fun getKey(context: Context): String = context.getString(keyResourceId)

    open fun getValue(context: Context): R? = getValue(context, null)
    open fun getValue(context: Context, defaultValue: R?): R? =
        valueGettingMethod(
            PreferenceManager.getDefaultSharedPreferences(context),
            getKey(context),
            defaultValue
        )
}