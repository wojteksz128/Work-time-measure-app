package net.wojteksz128.worktimemeasureapp.settings.converter

import net.wojteksz128.worktimemeasureapp.settings.Settings

interface ConfigurationConverter {
    fun convert(settings: Settings)
}