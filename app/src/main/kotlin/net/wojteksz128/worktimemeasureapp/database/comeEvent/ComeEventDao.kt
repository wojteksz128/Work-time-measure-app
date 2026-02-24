package net.wojteksz128.worktimemeasureapp.database.comeEvent

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.wojteksz128.worktimemeasureapp.database.EntityDao

private const val FIND_ALL_QUERY = "SELECT * FROM come_event ORDER BY startDate DESC"
private const val FIND_BY_ID_QUERY = "SELECT * FROM come_event WHERE id = :id"
private const val DELETE_ALL_QUERY = "DELETE FROM come_event"

@Dao
@Suppress("unused")
interface ComeEventDao : EntityDao<ComeEventDto> {

    @Query(FIND_ALL_QUERY)
    fun findAllInLiveData(): LiveData<List<ComeEventDto>>

    @Query(FIND_ALL_QUERY)
    suspend fun findAll(): List<ComeEventDto>

    @Query(FIND_BY_ID_QUERY)
    fun findByIdInLiveData(id: Int): LiveData<ComeEventDto>

    @Query(FIND_BY_ID_QUERY)
    suspend fun findById(id: Int): ComeEventDto?

    @Insert
    override suspend fun insert(entity: ComeEventDto): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: ComeEventDto)

    @Delete
    override suspend fun delete(entity: ComeEventDto)

    @Query(DELETE_ALL_QUERY)
    suspend fun deleteAll()
}
