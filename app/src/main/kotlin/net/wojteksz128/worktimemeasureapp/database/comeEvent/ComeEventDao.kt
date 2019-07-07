package net.wojteksz128.worktimemeasureapp.database.comeEvent

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ComeEventDao {

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAllInLiveData(): LiveData<List<ComeEvent>>

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAll(): List<ComeEvent>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findByIdInLiveData(id: Int): LiveData<ComeEvent>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findById(id: Int): ComeEvent

    @Insert
    fun insert(comeEvent: ComeEvent)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(comeEvent: ComeEvent)

    @Delete
    fun delete(comeEvent: ComeEvent)
}
