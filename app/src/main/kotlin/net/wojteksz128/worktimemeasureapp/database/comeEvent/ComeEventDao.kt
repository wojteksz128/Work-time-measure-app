package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.lifecycle.LiveData
import androidx.room.*
import net.wojteksz128.worktimemeasureapp.database.EntityDao

@Dao
@Suppress("unused")
// TODO: 30.09.2021 Make all suspended
interface ComeEventDao : EntityDao<ComeEventDto> {

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAllInLiveData(): LiveData<List<ComeEventDto>>

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAll(): List<ComeEventDto>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findByIdInLiveData(id: Int): LiveData<ComeEventDto>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findById(id: Int): ComeEventDto

    @Insert
    override fun insert(comeEvent: ComeEventDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override fun update(comeEvent: ComeEventDto)

    @Delete
    override fun delete(comeEvent: ComeEventDto)
}
