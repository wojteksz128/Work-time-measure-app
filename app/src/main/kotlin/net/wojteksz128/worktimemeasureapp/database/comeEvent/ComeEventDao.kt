package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
@Suppress("unused")
// TODO: 30.09.2021 Make all suspended
interface ComeEventDao {

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAllInLiveData(): LiveData<List<ComeEventDto>>

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAll(): List<ComeEventDto>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findByIdInLiveData(id: Int): LiveData<ComeEventDto>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findById(id: Int): ComeEventDto

    @Insert
    fun insert(comeEvent: ComeEventDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(comeEvent: ComeEventDto)

    @Delete
    fun delete(comeEvent: ComeEventDto)
}
