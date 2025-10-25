package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

open class SettingsItem<R>(
    val keyResourceId: Int,
    private val appContext: Context,
    private val valueGettingMethod: SharedPreferences.(String) -> R?,
    private val valueSettingMethod: SharedPreferences.Editor.(String, R) -> Unit,
) : SettingsNode() {
    override val childNodes: Set<SettingsItem<*>>
        get() = setOf(this)

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(appContext) }
    val key: String by lazy { appContext.getString(keyResourceId) }

    private var cachedValue: R? = null
    private var isCached = false

    var value: R
        get() = valueNullable
            ?: throw NullPointerException("Cannot read '$key' from Settings (maybe it's null)")
        set(value) {
            valueNullable = value
        }

    var valueNullable: R?
        get() {
            if (!isCached) {
                cachedValue = preferences.valueGettingMethod(key)
                isCached = true
            }
            return cachedValue
        }
        set(value) {
            if (valueNullable == value) return

            cachedValue = value
            isCached = true

            preferences.edit {
                if (value == null) {
                    remove(key)
                } else {
                    valueSettingMethod(key, value)
                }
            }

            _valueLiveData.postValue(value)
        }

    private val _valueLiveData: MutableLiveData<R?> by lazy {
        MutableLiveData(valueNullable)
    }
    val valueLiveData: LiveData<R?> = _valueLiveData

    fun invalidate() {
        isCached = false
        _valueLiveData.postValue(valueNullable)
    }
}
