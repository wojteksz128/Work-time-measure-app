package net.wojteksz128.worktimemeasureapp.settings.converter

interface VersionedConfigurationConverter : ConfigurationConverter {
    val fromVersion: Int
    val toVersion: Int
}
