package net.wojteksz128.worktimemeasureapp.database.workDay;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "work_day")
public class WorkDay {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date date;
    private Date beginSlot;
    private Date endSlot;
    private Date worktime;
    private double percentDeclaredTime;


    public WorkDay(int id, Date date, Date beginSlot, Date endSlot, Date worktime, double percentDeclaredTime) {
        this.id = id;
        this.date = date;
        this.beginSlot = beginSlot;
        this.endSlot = endSlot;
        this.worktime = worktime;
        this.percentDeclaredTime = percentDeclaredTime;
    }

    @Ignore
    public WorkDay(Date date) {
        this.date = date;
        this.beginSlot = WorkDayUtils.calculateBeginSlot(date);
        this.endSlot = WorkDayUtils.calculateEndSlot(date);
        this.worktime = new Date(0);
        this.percentDeclaredTime = 0;
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

    public Date getBeginSlot() {
        return beginSlot;
    }

    public void setBeginSlot(Date beginSlot) {
        this.beginSlot = beginSlot;
    }

    public Date getEndSlot() {
        return endSlot;
    }

    public void setEndSlot(Date endSlot) {
        this.endSlot = endSlot;
    }

    public Date getWorktime() {
        return worktime;
    }

    public void setWorktime(Date worktime) {
        this.worktime = worktime;
    }

    public double getPercentDeclaredTime() {
        return percentDeclaredTime;
    }

    public void setPercentDeclaredTime(double percentDeclaredTime) {
        this.percentDeclaredTime = percentDeclaredTime;
    }
}
