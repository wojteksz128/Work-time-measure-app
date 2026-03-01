package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

sealed interface WorkState {

    data object Loading : WorkState

    sealed class Loaded(
        open val workDay: WorkDay,
        open val workTimeBalance: WorkTimeBalance,
        dateTimeProvider: DateTimeProvider,
    ) : WorkState {

        val currentTime: ZonedDateTime = dateTimeProvider.currentTime

        open val todayWorkTime: Duration
            get() = workTimeBalance.todayWorkTime

        val standardRemainingWorkTime: Duration
            get() = workTimeBalance.standardRequiredToday - todayWorkTime

        val balancedRemainingWorkTime: Duration
            get() = workTimeBalance.standardRequiredToday - todayWorkTime - workTimeBalance.monthlyBalance

        val standardEndTime: ZonedDateTime
            get() = currentTime + standardRemainingWorkTime

        val balancedEndTime: ZonedDateTime
            get() = currentTime + balancedRemainingWorkTime
    }

    data class NotStarted(
        override val workDay: WorkDay,
        override val workTimeBalance: WorkTimeBalance,
        private val dateTimeProvider: DateTimeProvider,
    ) : Loaded(workDay, workTimeBalance, dateTimeProvider)

    data class InProgress(
        override val workDay: WorkDay,
        override val workTimeBalance: WorkTimeBalance,
        private val dateTimeProvider: DateTimeProvider,
    ) : Loaded(workDay, workTimeBalance, dateTimeProvider) {

        val nonFinishedEvent: ComeEvent = workDay.events.last { !it.isEnded }

        private val nonFinishedEventWorkTime: Duration
            get() = Duration.between(nonFinishedEvent.startDate, currentTime)

        override val todayWorkTime: Duration
            get() = super.todayWorkTime + nonFinishedEventWorkTime
    }

    data class Finished(
        override val workDay: WorkDay,
        override val workTimeBalance: WorkTimeBalance,
        private val dateTimeProvider: DateTimeProvider,
    ) : Loaded(workDay, workTimeBalance, dateTimeProvider)
}
