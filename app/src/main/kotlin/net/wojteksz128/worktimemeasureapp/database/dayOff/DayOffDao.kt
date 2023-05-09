@file:Suppress("unused")

package net.wojteksz128.worktimemeasureapp.database.dayOff

import androidx.lifecycle.LiveData
import androidx.room.*
import net.wojteksz128.worktimemeasureapp.database.EntityDao
import org.threeten.bp.LocalDate

@Dao
interface DayOffDao : EntityDao<DayOffDto> {

    @Transaction
    @Query("SELECT * FROM day_off ORDER BY startDate, finishDate")
    fun findAllInLiveData(): LiveData<List<DayOffDto>>

    @Transaction
    @Query("SELECT * FROM day_off ORDER BY startDate, finishDate")
    suspend fun findAll(): List<DayOffDto>

    @Transaction
    @Query("SELECT * FROM day_off WHERE id = :id")
    fun findByIdInLiveData(id: Long): LiveData<DayOffDto>

    @Transaction
    @Query("SELECT * FROM day_off WHERE id = :id")
    suspend fun findById(id: Long): DayOffDto

    @Transaction
    @Query("SELECT * FROM day_off WHERE :localDate BETWEEN startDate AND finishDate")
    suspend fun findByDate(localDate: LocalDate): DayOffDto?

    @Transaction
    @Query("SELECT * FROM day_off WHERE :startDate BETWEEN startDate AND finishDate OR :finishDate BETWEEN startDate AND finishDate OR startDate BETWEEN :startDate AND :finishDate")
    suspend fun findAllInDateRange(startDate: LocalDate, finishDate: LocalDate): List<DayOffDto>

    @Insert
    override suspend fun insert(entity: DayOffDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: DayOffDto)

    @Delete
    override suspend fun delete(entity: DayOffDto)
}