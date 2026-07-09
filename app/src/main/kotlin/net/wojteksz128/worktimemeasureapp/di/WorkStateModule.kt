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
    ): StateFlow<WorkState> {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val ticker = tickerFactory.create(scope)

        return dateTimeProvider.currentDateFlow
            .flatMapLatest { currentDate ->
                val workDayFlow = workDayRepository.getWorkDayByDateAsFlow(currentDate)
                workDayFlow.flatMapLatest { workDay ->
                    when {
                        workDay == null -> flow {
                            val day = WorkDay(currentDate)
                            val balance = workTimeBalanceCalculator.calculateBalanceForWorkDay(day)
                            emit(WorkState.NotStarted(day, balance, dateTimeProvider))
                        }

                        workDay.isWorkFinished() -> flow {
                            val balance =
                                workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay)
                            emit(WorkState.Finished(workDay, balance, dateTimeProvider))
                        }

                        workDay.events.any { !it.isEnded } -> flow {
                            val initialBalance =
                                workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay)
                            var lastState: WorkState =
                                WorkState.InProgress(workDay, initialBalance, dateTimeProvider)
                            emit(lastState)

                            ticker.collect {
                                val updatedBalance = (lastState as WorkState.Loaded).workTimeBalance
                                    .copy(currentTime = dateTimeProvider.currentTime)
                                lastState =
                                    WorkState.InProgress(workDay, updatedBalance, dateTimeProvider)
                                emit(lastState)
                            }
                        }

                        else -> flow {
                            val balance =
                                workTimeBalanceCalculator.calculateBalanceForWorkDay(workDay)
                            emit(WorkState.NotStarted(workDay, balance, dateTimeProvider))
                        }
                    }
                }
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), WorkState.Loading)
    }
}