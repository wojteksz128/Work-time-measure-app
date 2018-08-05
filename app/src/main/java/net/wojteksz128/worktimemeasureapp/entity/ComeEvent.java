package net.wojteksz128.worktimemeasureapp.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "come_event")
public class ComeEvent {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date date;
    private ComeEventType type;

    public ComeEvent(int id, Date date, ComeEventType type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

    @Ignore
    public ComeEvent(Date date, ComeEventType type) {
        this.date = date;
        this.type = type;
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
}
