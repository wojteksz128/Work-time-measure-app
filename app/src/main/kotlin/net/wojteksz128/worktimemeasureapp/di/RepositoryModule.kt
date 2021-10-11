package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.repository.ComeEventRepository
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideWorkDayRepository(
        workDayDao: WorkDayDao,
        workDayMapper: WorkDayMapper,
        workDayWithEventsMapper: WorkDayWithEventsMapper,
    ): WorkDayRepository {
        return WorkDayRepository(workDayDao, workDayMapper, workDayWithEventsMapper)
    }

    @Singleton
    @Provides
    fun provideComeEventRepository(
        comeEventDao: ComeEventDao,
        comeEventMapper: ComeEventMapper
    ): ComeEventRepository {
        return ComeEventRepository(comeEventDao, comeEventMapper)
    }
}
