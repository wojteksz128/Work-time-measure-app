package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.item.SettingsItemsNotifier

@Module
@InstallIn(ActivityComponent::class)
class ActivityUtilsModule {

    @Provides
    fun provideSettingsItemsNotifier(
        settings: Settings,
        @ActivityContext context: Context,
    ): SettingsItemsNotifier {
        return SettingsItemsNotifier(settings, context)
    }
}