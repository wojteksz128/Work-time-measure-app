package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.notification.NotificationChannelCreator
import net.wojteksz128.worktimemeasureapp.util.TimerManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationChannelCreator(@ApplicationContext context: Context): NotificationChannelCreator {
        return NotificationChannelCreator(context)
    }

    @Provides
    @Singleton
    fun provideTimerManager(@ApplicationContext context: Context): TimerManager {
        return TimerManager(context)
    }
}