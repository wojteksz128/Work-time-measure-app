package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.api.HolidayProvider
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.converter.ConfigurationConverterFrom0To1
import net.wojteksz128.worktimemeasureapp.settings.converter.VersionedConfigurationConverter
import net.wojteksz128.worktimemeasureapp.settings.item.AlarmStateSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.BooleanSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.DurationSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.EnumSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.InetAddressSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.IntFromStringSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.StringSettingsItem
import net.wojteksz128.worktimemeasureapp.settings.item.StringsArraySettingsItem
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Singleton
    @Provides
    @Named("settings_configuration_version")
    fun provideSettingsConfigurationVersion(@ApplicationContext context: Context): String =
        context.getString(R.string.settings_configuration_version)

    @Singleton
    @Provides
    fun provideSetOfVersionedConfigurationConverters(
        @ApplicationContext appContext: Context,
    ): Set<VersionedConfigurationConverter> = setOf(
        ConfigurationConverterFrom0To1(appContext)
    )

    @Singleton
    @Provides
    fun provideSettings(
        profile: Settings.ProfileSettings,
        workTime: Settings.WorkTimeSettings,
        daysOff: Settings.DaysOffSettings,
        sync: Settings.SyncSettings,
        internal: Settings.InternalSettings,
    ): Settings = Settings(profile, workTime, daysOff, sync, internal)

    @Singleton
    @Provides
    fun provideProfile(
        @Named("settings_profile_image") imagePath: StringSettingsItem,
        @Named("settings_profile_username") username: StringSettingsItem,
        @Named("settings_profile_email") email: StringSettingsItem,
    ): Settings.ProfileSettings = Settings.ProfileSettings(imagePath, username, email)

    @Singleton
    @Provides
    @Named("settings_profile_image")
    fun provideSettingsProfileImage(@ApplicationContext context: Context): StringSettingsItem =
        StringSettingsItem(R.string.settings_key_profile_image, context)

    @Singleton
    @Provides
    @Named("settings_profile_username")
    fun provideSettingsProfileUsername(@ApplicationContext context: Context): StringSettingsItem =
        StringSettingsItem(R.string.settings_key_profile_username, context)

    @Singleton
    @Provides
    @Named("settings_profile_email")
    fun provideSettingsProfileEmail(@ApplicationContext context: Context): StringSettingsItem =
        StringSettingsItem(R.string.settings_key_profile_email, context)

    @Singleton
    @Provides
    fun provideWorkTime(
        @Named("settings_workTime_notify_enable") notifyingEnabled: BooleanSettingsItem,
        @Named("settings_workTime_week") week: Settings.WorkTimeSettings.WeekSettings,
    ): Settings.WorkTimeSettings =
        Settings.WorkTimeSettings(notifyingEnabled, week)

    @Singleton
    @Provides
    @Named("settings_workTime_notify_enable")
    fun provideSettingsWorkTimeNotifyEnable(@ApplicationContext context: Context): BooleanSettingsItem =
        BooleanSettingsItem(R.string.settings_key_workTime_notify_enable, context)

    @Singleton
    @Provides
    @Named("settings_workTime_week")
    fun provideSettingsWorkTimeWeek(
        @Named("settings_workTime_firstWeekDay") firstWeekDay: StringSettingsItem,
        @Named("settings_workTime_week_daysOfWorkingWeek") daysOfWorkingWeek: StringsArraySettingsItem,
        @Named("settings_workTime_duration") duration: DurationSettingsItem,
    ): Settings.WorkTimeSettings.WeekSettings =
        Settings.WorkTimeSettings.WeekSettings(firstWeekDay, daysOfWorkingWeek, duration)

    @Singleton
    @Provides
    @Named("settings_workTime_firstWeekDay")
    fun provideSettingsWorkTimeFirstWeekDay(@ApplicationContext context: Context): StringSettingsItem =
        StringSettingsItem(R.string.settings_key_workTime_firstWeekDay, context)

    @Singleton
    @Provides
    @Named("settings_workTime_week_daysOfWorkingWeek")
    fun provideSettingsWorkTimeWeekDaysOfWorkingWeek(@ApplicationContext context: Context): StringsArraySettingsItem =
        StringsArraySettingsItem(R.string.settings_key_workTime_week_daysOfWorkingWeek, context)

    @Singleton
    @Provides
    @Named("settings_workTime_duration")
    fun provideSettingsWorkTimeDuration(@ApplicationContext context: Context): DurationSettingsItem =
        DurationSettingsItem(R.string.settings_key_workTime_duration, context)

    @Singleton
    @Provides
    fun provideDaysOff(
        @Named("settings_daysOff_public_syncWithApi") syncWithAPI: BooleanSettingsItem,
        @Named("settings_daysOff_public_provider") provider: EnumSettingsItem<HolidayProvider>,
        @Named("settings_daysOff_public_country") country: StringSettingsItem,
    ): Settings.DaysOffSettings = Settings.DaysOffSettings(syncWithAPI, provider, country)

    @Singleton
    @Provides
    @Named("settings_daysOff_public_syncWithApi")
    fun provideSettingsDaysOffPublicSyncWithApi(@ApplicationContext context: Context): BooleanSettingsItem =
        BooleanSettingsItem(R.string.settings_key_daysOff_public_syncWithApi, context)

    @Singleton
    @Provides
    @Named("settings_daysOff_public_provider")
    fun provideSettingsDaysOffPublicProvider(@ApplicationContext context: Context): EnumSettingsItem<HolidayProvider> =
        EnumSettingsItem(R.string.settings_key_daysOff_public_provider,
            context,
            HolidayProvider.entries.toTypedArray()
        )

    @Singleton
    @Provides
    @Named("settings_daysOff_public_country")
    fun provideSettingsDaysOffPublicCountry(@ApplicationContext context: Context): StringSettingsItem =
        StringSettingsItem(R.string.settings_key_daysOff_public_country, context)

    @Singleton
    @Provides
    fun provideSync(
        timeSync: Settings.SyncSettings.TimeSyncSettings,
    ): Settings.SyncSettings = Settings.SyncSettings(timeSync)

    @Singleton
    @Provides
    fun provideTimeSyncSettings(
        @Named("settings_sync_timeSync_enable") enabled: BooleanSettingsItem,
        @Named("settings_sync_timeSync_serverAddress") serverAddress: InetAddressSettingsItem,
    ): Settings.SyncSettings.TimeSyncSettings =
        Settings.SyncSettings.TimeSyncSettings(enabled, serverAddress)

    @Singleton
    @Provides
    @Named("settings_sync_timeSync_enable")
    fun provideSettingsSyncTimeSyncEnable(@ApplicationContext context: Context): BooleanSettingsItem =
        BooleanSettingsItem(R.string.settings_key_sync_timeSync_enable, context)

    @Singleton
    @Provides
    @Named("settings_sync_timeSync_serverAddress")
    fun provideSettingsSyncTimeSyncServerAddress(@ApplicationContext context: Context): InetAddressSettingsItem =
        InetAddressSettingsItem(R.string.settings_key_sync_timeSync_server, context)

    @Singleton
    @Provides
    fun provideInternal(
        @Named("settings_internal_alarmState") alarmState: AlarmStateSettingsItem,
        @Named("settings_internal_firstRun") firstRun: BooleanSettingsItem,
        @Named("settings_internal_configurationVersion") configurationVersion: IntFromStringSettingsItem,
    ): Settings.InternalSettings =
        Settings.InternalSettings(alarmState, firstRun, configurationVersion)

    @Singleton
    @Provides
    @Named("settings_internal_alarmState")
    fun provideSettingsInternalAlarmState(@ApplicationContext context: Context): AlarmStateSettingsItem =
        AlarmStateSettingsItem(R.string.settings_key_internal_alarmSetTime, context)

    @Singleton
    @Provides
    @Named("settings_internal_firstRun")
    fun provideSettingsInternalFirstRun(@ApplicationContext context: Context): BooleanSettingsItem =
        BooleanSettingsItem(R.string.settings_key_internal_firstRun, context, true)

    @Singleton
    @Provides
    @Named("settings_internal_configurationVersion")
    fun provideSettingsInternalConfigurationVersion(@ApplicationContext context: Context): IntFromStringSettingsItem =
        IntFromStringSettingsItem(R.string.settings_key_internal_configurationVersion, context)
}