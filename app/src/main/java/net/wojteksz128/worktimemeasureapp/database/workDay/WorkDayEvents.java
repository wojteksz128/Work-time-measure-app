package net.wojteksz128.worktimemeasureapp.database.workDay;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class WorkDayEvents {

    @Embedded
    private WorkDay workDay;

    @Relation(parentColumn = "id", entityColumn = "workDayId", entity = ComeEvent.class)
    private List<ComeEvent> events;

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }

    public List<ComeEvent> getEvents() {
        Collections.sort(events, Collections.<ComeEvent>reverseOrder());
        return events;
    }

    public void setEvents(List<ComeEvent> events) {
        this.events = events;
    }
}
