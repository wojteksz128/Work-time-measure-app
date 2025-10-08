package net.wojteksz128.worktimemeasureapp.settings.converter

import net.wojteksz128.worktimemeasureapp.settings.Settings
import javax.inject.Inject

class ConfigurationConverterFactory @Inject constructor(
    private val configurationConverters: Set<@JvmSuppressWildcards VersionedConfigurationConverter>,
) {

    fun create(fromVersion: Int?, toVersion: Int): ConfigurationConverter =
        CompoundConfigurationConverter(
            configurationConverters.filter {
                it.fromVersion <= (fromVersion ?: 0) && it.toVersion >= toVersion
            }.sortedBy { it.fromVersion }
        )

    class CompoundConfigurationConverter(
        private val converters: List<VersionedConfigurationConverter>,
    ) : ConfigurationConverter {

        override fun convert(settings: Settings) = converters.forEach { it.convert(settings) }
    }
}