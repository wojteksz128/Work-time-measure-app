package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.room.Embedded
import androidx.room.Relation
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto

class WorkDayWithEventsDto(
    @Embedded val workDay: WorkDayDto,

    @Relation(
        parentColumn = "id",
        entityColumn = "workDayId",
        entity = ComeEventDto::class
    )
    val events: List<ComeEventDto> = listOf() // TODO: 30.09.2021 Remove default value after implement mapper
) {

    // TODO: 30.09.2021 Remove after implement mapper
    fun hasEventsEnded() = events.all { it.isEnded }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDayWithEventsDto

        if (workDay != other.workDay) return false

        return true
    }

    override fun hashCode(): Int {
        return workDay.hashCode()
    }


}
