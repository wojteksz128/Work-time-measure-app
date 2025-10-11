package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalanceCalculator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkStateModule {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideWorkStateFlow(
        workDayRepository: WorkDayRepository,
        dateTimeProvider: DateTimeProvider,
        tickerFactory: TickerFactory,
        workTimeBalanceCalculator: WorkTimeBalanceCalculator,
    ): StateFlow<WorkState?> {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val ticker = tickerFactory.create(scope)

        val workDayFlow = workDayRepository.getWorkDayByDateAsFlow(dateTimeProvider.currentDate)

        return workDayFlow.flatMapLatest { workDay ->
            if (workDay == null || workDay.isWorkFinished()) {
                flow {
                    val workDay = workDay ?: WorkDay(dateTimeProvider.currentDate)
                    val balance = workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay)
                    emit(WorkState(workDay, balance))
                }
            } else {
                flow {
                    val initialBalance =
                        workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay)
                    var lastState = WorkState(workDay, initialBalance)
                    emit(lastState)

                    ticker.collect {
                        val updatedBalance =
                            workTimeBalanceCalculator.updateTodayBalance(
                                workDay,
                                lastState.workTimeBalance
                            )
                        lastState = WorkState(workDay, updatedBalance)
                        emit(lastState)
                    }
                }
            }
        }.stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
    }
}