package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.lifecycle.LiveData
import androidx.room.*
import net.wojteksz128.worktimemeasureapp.database.EntityDao

@Dao
@Suppress("unused")
interface ComeEventDao : EntityDao<ComeEventDto> {

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    fun findAllInLiveData(): LiveData<List<ComeEventDto>>

    @Query("SELECT * FROM come_event ORDER BY startDate DESC")
    suspend fun findAll(): List<ComeEventDto>

    @Query("SELECT * FROM come_event WHERE id = :id")
    fun findByIdInLiveData(id: Int): LiveData<ComeEventDto>

    @Query("SELECT * FROM come_event WHERE id = :id")
    suspend fun findById(id: Int): ComeEventDto

    @Insert
    override suspend fun insert(entity: ComeEventDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: ComeEventDto)

    @Delete
    override suspend fun delete(entity: ComeEventDto)
}
