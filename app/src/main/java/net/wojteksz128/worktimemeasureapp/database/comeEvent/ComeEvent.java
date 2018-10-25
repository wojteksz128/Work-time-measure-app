package net.wojteksz128.worktimemeasureapp.database.comeEvent;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay;

import java.util.Date;

@Entity(tableName = "come_event")
public class ComeEvent implements Comparable<ComeEvent> {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Date startDate;
    private Date endDate;
    private Date duration;
    private int workDayId;


    public ComeEvent(Long id, Date startDate, Date endDate, Date duration, int workDayId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.workDayId = workDayId;
    }

    @Ignore
    public ComeEvent(Date startDate, Date endDate, Date duration, int workDayId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.workDayId = workDayId;
    }

    @Ignore
    public ComeEvent(Date startDate, WorkDay workDay) {
        this.startDate = startDate;
        this.workDayId = workDay.getId();
    }

    public Long getId() {
        return id;
    }

    public int getWorkDayId() {
        return workDayId;
    }

    public void setWorkDayId(int workDayId) {
        this.workDayId = workDayId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public boolean isEnded() {
        return this.endDate != null && this.duration != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComeEvent event = (ComeEvent) o;

        if (workDayId != event.workDayId) return false;
        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        return startDate.equals(event.startDate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + startDate.hashCode();
        result = 31 * result + workDayId;
        return result;
    }

    @Override
    public int compareTo(@NonNull ComeEvent comeEvent) {
        return (int) (this.getStartDate().getTime() - comeEvent.getStartDate().getTime());
    }
}
