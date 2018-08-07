package net.wojteksz128.worktimemeasureapp.database.workDay;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;

import java.util.List;

public class WorkDayEvents {

    @Embedded
    public WorkDay workDay;

    @Relation(parentColumn = "id", entityColumn = "workDayId", entity = ComeEvent.class)
    public List<ComeEvent> events;
}
