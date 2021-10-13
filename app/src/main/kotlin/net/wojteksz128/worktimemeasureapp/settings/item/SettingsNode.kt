package net.wojteksz128.worktimemeasureapp.settings.item

interface SettingsNode {
    val childNodes: Set<SettingsItem<*>>
}