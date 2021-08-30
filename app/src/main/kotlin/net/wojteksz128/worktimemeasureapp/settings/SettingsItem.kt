package net.wojteksz128.worktimemeasureapp.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp

open class SettingsItem<out R>(
    private val keyResourceId: Int,
    private val valueGettingMethod: (SharedPreferences, String) -> R?
) {

    val key: String
        get() = context.getString(keyResourceId)

    val value: R
        get() = valueNullable
            ?: throw NullPointerException("Cannot read '$key' from Settings (maybe it's null)")

    val valueNullable: R?
        get() = valueGettingMethod(PreferenceManager.getDefaultSharedPreferences(context), key)

}

private val context: Context
    get() = WorkTimeMeasureApp.context