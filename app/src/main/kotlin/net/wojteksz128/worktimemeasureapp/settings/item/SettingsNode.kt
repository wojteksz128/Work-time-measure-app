package net.wojteksz128.worktimemeasureapp.settings.item

abstract class SettingsNode(vararg nodes: SettingsNode) {
    open val childNodes: Set<BaseSettingsItem<*>> = nodes.flatMap { it.childNodes }.toSet()
}