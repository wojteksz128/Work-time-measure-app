package net.wojteksz128.worktimemeasureapp.database.workDay

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import java.util.*

class WorkDayEvents {

    @Embedded
    var workDay: WorkDay? = null

    @Relation(parentColumn = "id", entityColumn = "workDayId", entity = ComeEvent::class)
    private var events: List<ComeEvent>? = null

    fun getEvents(): List<ComeEvent> {
        Collections.sort(events!!, Collections.reverseOrder())
        return events
    }

    fun setEvents(events: List<ComeEvent>) {
        this.events = events
    }

    fun hasEventsEnded(): Boolean {
        for (comeEvent in this.events!!) {
            if (!comeEvent.isEnded) {
                return false
            }
        }
        return true
    }
}
