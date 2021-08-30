package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
import net.wojteksz128.worktimemeasureapp.settings.Settings

open class SettingsItem<out R>(
    val keyResourceId: Int,
    private val valueGettingMethod: (SharedPreferences, String) -> R?
) {

    internal var changed: Boolean = false
    private var readValue: R? = null

    init {
        Settings.registerItem(this)
    }

    val key: String
        get() = appContext.getString(keyResourceId)

    val value: R
        get() = valueNullable
            ?: throw NullPointerException("Cannot read '$key' from Settings (maybe it's null)")

    val valueNullable: R?
        get() {
            if (changed || readValue == null) {
                readValue = valueGettingMethod(
                    PreferenceManager.getDefaultSharedPreferences(
                        appContext
                    ), key
                )
                changed = false
            }
            return readValue
        }
}

private val appContext: Context
    get() = WorkTimeMeasureApp.context