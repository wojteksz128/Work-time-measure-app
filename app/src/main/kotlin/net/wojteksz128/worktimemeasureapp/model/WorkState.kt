package net.wojteksz128.worktimemeasureapp.model

import net.wojteksz128.worktimemeasureapp.util.datetime.floorToSeconds
import net.wojteksz128.worktimemeasureapp.util.model.extension.finishedEventsDuration
import net.wojteksz128.worktimemeasureapp.util.model.extension.notEndedEvent
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

sealed interface WorkState {

    data object Loading : WorkState

    sealed class Loaded(
        open val workDay: WorkDay,
        open val workTimeRequirements: WorkTimeRequirements,
        private val currentTime: ZonedDateTime,
    ) : WorkState {

        open val todayWorkTime: Duration
            get() = workDay.finishedEventsDuration.floorToSeconds()

        val standardWorkTime by lazy {
            WorkTimeInformation(
                this.workTimeRequirements.standardRequiredToday,
                this::todayWorkTime,
                this::currentTime
            )
        }

        val balancedWorkTime by lazy {
            WorkTimeInformation(
                this.workTimeRequirements.balancedRequiredToday,
                this::todayWorkTime,
                this::currentTime
            )
        }
    }

    data class NotStarted(
        override val workDay: WorkDay,
        override val workTimeRequirements: WorkTimeRequirements,
        val currentTime: ZonedDateTime,
    ) : Loaded(workDay, workTimeRequirements, currentTime)

    data class InProgress(
        override val workDay: WorkDay,
        override val workTimeRequirements: WorkTimeRequirements,
        val currentTime: ZonedDateTime,
    ) : Loaded(workDay, workTimeRequirements, currentTime) {

        // TODO: Jak mogę zabezpieczyć, że będzie istnieć tylko jeden obiekt, który nie został ukończony?
        val nonFinishedEvent = workDay.notEndedEvent!!

        private val nonFinishedEventWorkTime: Duration
            get() = Duration.between(nonFinishedEvent.startDate, currentTime)

        override val todayWorkTime: Duration
            get() = (super.todayWorkTime + nonFinishedEventWorkTime).floorToSeconds()
    }

    data class Finished(
        override val workDay: WorkDay,
        override val workTimeRequirements: WorkTimeRequirements,
        val currentTime: ZonedDateTime,
    ) : Loaded(workDay, workTimeRequirements, currentTime)
}
