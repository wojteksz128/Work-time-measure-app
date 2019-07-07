package net.wojteksz128.worktimemeasureapp.database.workDay

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent

class WorkDayEvents {

    @Embedded
    var workDay: WorkDay

    @Relation(parentColumn = "id", entityColumn = "workDayId", entity = ComeEvent::class)
    lateinit var events: List<ComeEvent>

    constructor(workDay: WorkDay) {
        this.workDay = workDay
    }

    fun hasEventsEnded() = events.all { it.isEnded }
}
