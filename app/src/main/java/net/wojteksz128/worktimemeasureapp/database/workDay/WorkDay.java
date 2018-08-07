package net.wojteksz128.worktimemeasureapp.database.workDay;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

@Entity(tableName = "work_day")
public class WorkDay {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date date;
    private Duration worktime;
    private double percentDeclaredTime;
    @Relation(parentColumn = "id", entityColumn = "workDayId")
    private List<ComeEvent> events;


    public WorkDay(int id, Date date, Duration worktime, double percentDeclaredTime) {
        this.id = id;
        this.date = date;
        this.worktime = worktime;
        this.percentDeclaredTime = percentDeclaredTime;
    }

    @Ignore
    public WorkDay(Date date, Duration worktime, double percentDeclaredTime) {
        this.date = date;
        this.worktime = worktime;
        this.percentDeclaredTime = percentDeclaredTime;
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

    public Duration getWorktime() {
        return worktime;
    }

    public void setWorktime(Duration worktime) {
        this.worktime = worktime;
    }

    public double getPercentDeclaredTime() {
        return percentDeclaredTime;
    }

    public void setPercentDeclaredTime(double percentDeclaredTime) {
        this.percentDeclaredTime = percentDeclaredTime;
    }
}
