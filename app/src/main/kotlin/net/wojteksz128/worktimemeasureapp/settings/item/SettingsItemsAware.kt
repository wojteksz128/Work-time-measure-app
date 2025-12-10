package net.wojteksz128.worktimemeasureapp.settings.item

import android.content.Context

abstract class SettingsItemsAware(vararg childItems: SettingsNode) {
    private val items: MutableMap<Int, BaseSettingsItem<*>> = mutableMapOf()

    init {
        childItems.flatMap { it.childNodes }.forEach { registerItem(it) }
    }

    private fun registerItem(settingsItem: BaseSettingsItem<*>) {
        items[settingsItem.keyResourceId] = settingsItem
    }

    fun notifyItemChanged(key: String?, context: Context?) {
        if (key != null && context != null) {
            items.filterKeys { context.getString(it) == key }.values.forEach { it.invalidate() }
        }
    }
}
