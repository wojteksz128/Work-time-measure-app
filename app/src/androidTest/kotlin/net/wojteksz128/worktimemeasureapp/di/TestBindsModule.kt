package net.wojteksz128.worktimemeasureapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.util.FakeDateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestBindsModule {

    @Binds
    @Singleton
    abstract fun bindDateTimeProvider(
        fakeDateTimeProvider: FakeDateTimeProvider,
    ): DateTimeProvider
}