package net.wojteksz128.worktimemeasureapp.database.workDay;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WorkDayDao {

    @Transaction
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    LiveData<List<WorkDayEvents>> findAllInLiveData();

    @Transaction
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    List<WorkDayEvents> findAll();

    @Transaction
    @Query("SELECT * FROM work_day WHERE id = :id")
    LiveData<WorkDayEvents> findByIdInLiveData(int id);

    @Transaction
    @Query("SELECT * FROM work_day WHERE id = :id")
    WorkDayEvents findById(int id);

    @Insert
    void insert(WorkDay workDay);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(WorkDay workDay);

    @Delete
    void delete(WorkDay workDay);
}
