package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.model.WorkState
import net.wojteksz128.worktimemeasureapp.model.WorkTimeRequirements
import net.wojteksz128.worktimemeasureapp.repository.WorkDayRepository
import net.wojteksz128.worktimemeasureapp.service.WorkTimeRequirementsCalculator
import net.wojteksz128.worktimemeasureapp.util.coroutines.TickerFactory
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.model.extension.isNew
import net.wojteksz128.worktimemeasureapp.util.model.extension.isWorkFinished
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
        timeRequirementsCalculator: WorkTimeRequirementsCalculator,
    ): StateFlow<WorkState> {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val ticker = tickerFactory.create(scope)

        return dateTimeProvider.currentDateFlow
            .flatMapLatest { currentDate ->
                val workDayFlow = workDayRepository.getWorkDayByDateAsFlow(currentDate)
                workDayFlow.flatMapLatest { workDay ->
                    val currentWorkDay = workDay ?: WorkDay(currentDate)
                    val workTimeRequirements = timeRequirementsCalculator.calculate(currentWorkDay)

                    when {
                        currentWorkDay.isNew -> flow {
                            emitNotStarted(currentWorkDay, workTimeRequirements, dateTimeProvider)
                        }

                        currentWorkDay.isWorkFinished -> flow {
                            emitFinished(currentWorkDay, workTimeRequirements, dateTimeProvider)
                        }

                        else -> flow {
                            emitInProgressAndPlanUpdates(
                                currentWorkDay,
                                workTimeRequirements,
                                dateTimeProvider,
                                ticker
                            )
                        }
                    }
                }
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), WorkState.Loading)
    }

    private suspend fun FlowCollector<WorkState.NotStarted>.emitNotStarted(
        workDay: WorkDay,
        workTimeRequirements: WorkTimeRequirements,
        dateTimeProvider: DateTimeProvider,
    ) {
        emit(WorkState.NotStarted(workDay, workTimeRequirements, dateTimeProvider.currentTime))
    }

    private suspend fun FlowCollector<WorkState.Finished>.emitFinished(
        currentWorkDay: WorkDay,
        workTimeRequirements: WorkTimeRequirements,
        dateTimeProvider: DateTimeProvider,
    ) {
        emit(WorkState.Finished(currentWorkDay, workTimeRequirements, dateTimeProvider.currentTime))
    }

    private suspend fun FlowCollector<WorkState.InProgress>.emitInProgressAndPlanUpdates(
        currentWorkDay: WorkDay,
        workTimeRequirements: WorkTimeRequirements,
        dateTimeProvider: DateTimeProvider,
        ticker: SharedFlow<Unit>,
    ) {
        var lastState =
            WorkState.InProgress(
                currentWorkDay,
                workTimeRequirements,
                dateTimeProvider.currentTime
            )
        emit(lastState)

        ticker.collect {
            lastState = lastState.copy(currentTime = dateTimeProvider.currentTime)
            emit(lastState)
        }
    }
}