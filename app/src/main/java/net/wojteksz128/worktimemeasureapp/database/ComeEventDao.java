package net.wojteksz128.worktimemeasureapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ComeEventDao {

    @Query("SELECT * FROM come_event ORDER BY date DESC")
    LiveData<List<ComeEvent>> findAllInLiveData();

    @Query("SELECT * FROM come_event ORDER BY date DESC")
    List<ComeEvent> findAll();

    @Query("SELECT * FROM come_event WHERE id = :id")
    LiveData<ComeEvent> findByIdInLiveData(int id);

    @Query("SELECT * FROM come_event WHERE id = :id")
    ComeEvent findById(int id);

    @Insert
    void insert(ComeEvent comeEvent);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ComeEvent comeEvent);
}
