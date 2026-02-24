@file:Suppress("unused")

package net.wojteksz128.worktimemeasureapp.database.dayOff

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import net.wojteksz128.worktimemeasureapp.database.EntityDao
import org.threeten.bp.LocalDate

private const val FIND_ALL_QUERY = "SELECT * FROM day_off ORDER BY startDate, finishDate"
private const val FIND_BY_ID_QUERY = "SELECT * FROM day_off WHERE id = :id"
private const val FIND_BY_DATE_QUERY =
    "SELECT * FROM day_off WHERE :localDate BETWEEN startDate AND finishDate"
private const val FIND_ALL_IN_DATE_RANGE_QUERY =
    "SELECT * FROM day_off WHERE :startDate BETWEEN startDate AND finishDate OR :finishDate BETWEEN startDate AND finishDate OR startDate BETWEEN :startDate AND :finishDate"

@Dao
interface DayOffDao : EntityDao<DayOffDto> {

    @Transaction
    @Query(FIND_ALL_QUERY)
    fun findAllInLiveData(): LiveData<List<DayOffDto>>

    @Transaction
    @Query(FIND_ALL_QUERY)
    suspend fun findAll(): List<DayOffDto>

    @Transaction
    @Query(FIND_BY_ID_QUERY)
    fun findByIdInLiveData(id: Long): LiveData<DayOffDto>

    @Transaction
    @Query(FIND_BY_ID_QUERY)
    suspend fun findById(id: Long): DayOffDto

    @Transaction
    @Query(FIND_BY_ID_QUERY)
    suspend fun findByIdOrNull(id: Long): DayOffDto?

    @Transaction
    @Query(FIND_BY_DATE_QUERY)
    suspend fun findByDate(localDate: LocalDate): DayOffDto?

    @Transaction
    @Query(FIND_ALL_IN_DATE_RANGE_QUERY)
    suspend fun findAllInDateRange(startDate: LocalDate, finishDate: LocalDate): List<DayOffDto>

    @Insert
    override suspend fun insert(entity: DayOffDto): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: DayOffDto)

    @Delete
    override suspend fun delete(entity: DayOffDto)
}