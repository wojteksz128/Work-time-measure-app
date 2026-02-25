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

    /** The current value, or null if not set. Used e.g. during backup export. */
    abstract val valueNullable: R?

    /** Method for invalidating the cached value, forcing it to be reloaded. */
    abstract fun invalidate()

    /**
     * Restores the value from a raw string read from a backup file.
     * Null removes the key from storage.
     */
    abstract fun restoreValue(rawValue: String?)
}