package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

open class SettingsItem<R>(
    val keyResourceId: Int,
    private val appContext: Context,
    private val valueGettingMethod: (SharedPreferences, String) -> R?,
    private val valueSettingMethod: (SharedPreferences.Editor, String, R) -> Unit,
) : SettingsNode() {
    override val childNodes: Set<SettingsItem<*>>
        get() = setOf(this)

    internal var changed: Boolean = false
    private var readValue: R? = null

    val key: String
        get() = appContext.getString(keyResourceId)

    var value: R
        get() = valueNullable
            ?: throw NullPointerException("Cannot read '$key' from Settings (maybe it's null)")
        set(value) {
            valueNullable = value
        }


    var valueNullable: R?
        get() {
            if (changed || readValue == null) {
                val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
                readValue = valueGettingMethod(preferences, key)
                changed = false
            }
            return readValue
        }
        set(value) {
            val editor = PreferenceManager.getDefaultSharedPreferences(appContext).edit()
            if (value == null)
                editor.remove(key)
            else
                valueSettingMethod(editor, key, value)

            editor.apply()
            changed = true
        }
}