package net.wojteksz128.worktimemeasureapp.database.workDay

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import java.util.*

class WorkDayEvents(
        @Embedded val workDay: WorkDay,
        @Relation(parentColumn = "id", entityColumn = "workDayId", entity = ComeEvent::class)
        private val events: List<ComeEvent>
) {

    init {
        Collections.sort(events, Collections.reverseOrder())
    }

    fun hasEventsEnded() = events.all { it.isEnded }
}
