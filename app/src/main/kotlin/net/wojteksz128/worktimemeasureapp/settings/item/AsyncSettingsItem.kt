package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A specialized class for handling settings that require an asynchronous operation
 * to retrieve the value (e.g., a network operation or a heavy computation).
 *
 * @param R The target value type (e.g., InetAddress).
 * @param S The type stored in SharedPreferences (e.g., String).
 */
open class AsyncSettingsItem<R, S>(
    keyResourceId: Int,
    appContext: Context,
    private val asyncTransformer: suspend (S) -> R?,
    private val rawValueGetter: SharedPreferences.(String) -> S?,
    private val rawValueSetter: SharedPreferences.Editor.(String, R) -> Unit,
) : BaseSettingsItem<R>(keyResourceId, appContext) {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            appContext
        )
    }

    /**
     * Safely retrieves the value by performing the operation on a background thread.
     * This must be called from a coroutine scope (e.g., `viewModelScope.launch`).
     */
    suspend fun getValueAsync(): R? {
        val rawValue = preferences.rawValueGetter(key)
        val result = rawValue?.let {
            withContext(Dispatchers.IO) {
                try {
                    asyncTransformer(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        _valueLiveData.postValue(result)
        return result
    }

    /**
     * Sets the value. The provided value of type R is converted to type S for storage.
     */
    fun setValue(value: R?) {
        preferences.edit {
            if (value == null) {
                remove(key)
            } else {
                rawValueSetter(key, value)
            }
        }
        _valueLiveData.postValue(value)
    }

    private val _valueLiveData by lazy { MutableLiveData<R?>() }
    override val valueLiveData = _valueLiveData

    override fun invalidate() {
        // In an async item, invalidation simply means the next call to `getValueAsync`
        // will perform a full fetch. We don't trigger it automatically.
    }
}