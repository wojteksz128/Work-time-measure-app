package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

open class SettingsItem<R>(
    keyResourceId: Int,
    appContext: Context,
    private val valueGetter: SharedPreferences.(String) -> R?,
    private val valueSetter: SharedPreferences.Editor.(String, R) -> Unit,
    private val fromString: (String) -> R? = { null },
) : BaseSettingsItem<R>(keyResourceId, appContext) {

    protected val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            appContext
        )
    }

    private var cachedValue: R? = null
    private var isCached = false

    open var value: R
        get() = valueNullable
            ?: throw NullPointerException("Cannot read '$key' from Settings (maybe it's null)")
        set(value) {
            valueNullable = value
        }

    override var valueNullable: R?
        get() {
            if (!isCached) {
                cachedValue = preferences.valueGetter(key)
                isCached = true
            }
            return cachedValue
        }
        set(value) {
            if (isCached && cachedValue == value) return
            cachedValue = value
            isCached = true
            preferences.edit {
                if (value == null) remove(key) else valueSetter(key, value)
            }
            _valueLiveData.postValue(value)
        }

    private val _valueLiveData by lazy { MutableLiveData<R?>() }
    override val valueLiveData = _valueLiveData

    override fun invalidate() {
        isCached = false
        _valueLiveData.postValue(valueNullable)
    }

    override fun restoreValue(rawValue: String?) {
        valueNullable = rawValue?.let { fromString(it) }
    }
}
