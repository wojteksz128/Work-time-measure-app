package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

abstract class SettingsItemsAware {
    private val items: MutableMap<Int, SettingsItem<*>> = mutableMapOf()

    fun registerItem(settingsItem: SettingsItem<*>) {
        items[settingsItem.keyResourceId] = settingsItem
    }

    fun notifyItem(key: String?, context: Context?) {
        if (context != null) {
            items.filterKeys { context.getString(it) == key }.values.forEach { it.changed = true }
        }
    }
}