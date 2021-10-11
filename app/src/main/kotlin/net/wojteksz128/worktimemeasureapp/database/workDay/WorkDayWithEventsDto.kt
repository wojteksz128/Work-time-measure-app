package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.room.Embedded
import androidx.room.Relation
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto

data class WorkDayWithEventsDto(
    @Embedded val workDay: WorkDayDto,

    @Relation(
        parentColumn = "id",
        entityColumn = "workDayId",
        entity = ComeEventDto::class
    )
    val events: List<ComeEventDto>
)
