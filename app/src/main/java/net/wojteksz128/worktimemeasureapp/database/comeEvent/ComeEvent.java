package net.wojteksz128.worktimemeasureapp.database.comeEvent;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay;

import java.util.Date;

@Entity(tableName = "come_event")
public class ComeEvent {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date date;
    private ComeEventType type;
    private int workDayId;

    public ComeEvent(int id, Date date, ComeEventType type, Integer workDayId) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.workDayId = workDayId;
    }

    @Ignore
    public ComeEvent(Date date, ComeEventType type, WorkDay workDay) {
        this.date = date;
        this.type = type;
        this.workDayId = workDay.getId();
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ComeEventType getType() {
        return type;
    }

    public void setType(ComeEventType type) {
        this.type = type;
    }

    public int getWorkDayId() {
        return workDayId;
    }

    public void setWorkDayId(int workDayId) {
        this.workDayId = workDayId;
    }
}
