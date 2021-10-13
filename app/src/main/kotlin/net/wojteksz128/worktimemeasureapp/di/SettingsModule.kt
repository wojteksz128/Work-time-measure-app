package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.item.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Singleton
    @Provides
    fun provideSettings(
        Profile: Settings.ProfileSettings,
        WorkTime: Settings.WorkTimeSettings,
        Sync: Settings.SyncSettings,
        Internal: Settings.InternalSettings,
    ): Settings = Settings(Profile, WorkTime, Sync, Internal)

    @Singleton
    @Provides
    fun provideProfile(
        @Named("settings_profile_image") ImagePath: StringSettingsItem,
        @Named("settings_profile_username") Username: StringSettingsItem,
        @Named("settings_profile_email") Email: StringSettingsItem,
    ): Settings.ProfileSettings = Settings.ProfileSettings(ImagePath, Username, Email)

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
        @Named("settings_workTime_notify_enable") NotifyingEnabled: BooleanSettingsItem,
        @Named("settings_workTime_duration") Duration: DurationSettingsItem,
        @Named("settings_workTime_firstWeekDay") FirstWeekDay: IntFromStringSettingsItem,
    ): Settings.WorkTimeSettings =
        Settings.WorkTimeSettings(NotifyingEnabled, Duration, FirstWeekDay)

    @Singleton
    @Provides
    @Named("settings_workTime_notify_enable")
    fun provideSettingsWorkTimeNotifyEnable(@ApplicationContext context: Context): BooleanSettingsItem =
        BooleanSettingsItem(R.string.settings_key_workTime_notify_enable, context)

    @Singleton
    @Provides
    @Named("settings_workTime_duration")
    fun provideSettingsWorkTimeDuration(@ApplicationContext context: Context): DurationSettingsItem =
        DurationSettingsItem(R.string.settings_key_workTime_duration, context)

    @Singleton
    @Provides
    @Named("settings_workTime_firstWeekDay")
    fun provideSettingsWorkTimeFirstWeekDay(@ApplicationContext context: Context): IntFromStringSettingsItem =
        IntFromStringSettingsItem(R.string.settings_key_workTime_firstWeekDay, context)

    @Singleton
    @Provides
    fun provideSync(
        TimeSync: Settings.SyncSettings.TimeSyncSettings,
    ): Settings.SyncSettings = Settings.SyncSettings(TimeSync)

    @Singleton
    @Provides
    fun provideTimeSyncSettings(
        @Named("settings_sync_timeSync_enable") Enabled: BooleanSettingsItem,
        @Named("settings_sync_timeSync_serverAddress") ServerAddress: StringSettingsItem,
    ): Settings.SyncSettings.TimeSyncSettings =
        Settings.SyncSettings.TimeSyncSettings(Enabled, ServerAddress)

    @Singleton
    @Provides
    @Named("settings_sync_timeSync_enable")
    fun provideSettingsSyncTimeSyncEnable(@ApplicationContext context: Context): BooleanSettingsItem =
        BooleanSettingsItem(R.string.settings_key_sync_timeSync_enable, context)

    @Singleton
    @Provides
    @Named("settings_sync_timeSync_serverAddress")
    fun provideSettingsSyncTimeSyncServerAddress(@ApplicationContext context: Context): StringSettingsItem =
        StringSettingsItem(R.string.settings_key_sync_timeSync_server, context)

    @Singleton
    @Provides
    fun provideInternal(
        @Named("settings_internal_alarmState") AlarmState: AlarmStateSettingsItem,
    ): Settings.InternalSettings = Settings.InternalSettings(AlarmState)

    @Singleton
    @Provides
    @Named("settings_internal_alarmState")
    fun provideSettingsInternalAlarmState(@ApplicationContext context: Context): AlarmStateSettingsItem =
        AlarmStateSettingsItem(R.string.settings_key_internal_alarmSetTime, context)
}