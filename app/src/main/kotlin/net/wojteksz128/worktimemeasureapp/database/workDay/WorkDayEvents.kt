package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.room.Embedded
import androidx.room.Relation
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent

class WorkDayEvents(@Embedded var workDay: WorkDay) {

    @Relation(parentColumn = "id", entityColumn = "workDayId", entity = ComeEvent::class)
    lateinit var events: List<ComeEvent>

    fun hasEventsEnded() = events.all { it.isEnded }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDayEvents

        if (workDay != other.workDay) return false
        if (events != other.events) return false

        return true
    }

    override fun hashCode(): Int {
        var result = workDay.hashCode()
        result = 31 * result + events.hashCode()
        return result
    }


}
