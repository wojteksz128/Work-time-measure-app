package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context
import androidx.lifecycle.LiveData

/**
 * Abstract base class for all setting types.
 * It defines common properties but doesn't implement the value access logic.
 */
abstract class BaseSettingsItem<R>(
    val keyResourceId: Int,
    protected val appContext: Context,
) : SettingsNode() {
    override val childNodes: Set<BaseSettingsItem<*>>
        get() = setOf(this)

    val key: String by lazy { appContext.getString(keyResourceId) }

    /**
     * LiveData that will notify about value changes.
     * Subclasses are responsible for updating it.
     */
    abstract val valueLiveData: LiveData<R?>

    /**
     * Method for invalidating the cached value, forcing it to be reloaded.
     */
    abstract fun invalidate()
}