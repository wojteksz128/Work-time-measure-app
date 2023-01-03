@file:Suppress("unused")

package net.wojteksz128.worktimemeasureapp.database.dayOff

import androidx.lifecycle.LiveData
import androidx.room.*
import net.wojteksz128.worktimemeasureapp.database.EntityDao
import org.threeten.bp.Month

@Dao
interface DayOffDao : EntityDao<DayOffDto> {

    @Transaction
    @Query("SELECT * FROM day_off ORDER BY startYear, finishYear, startDay, startMonth, finishDay, finishMonth")
    fun findAllInLiveData(): LiveData<List<DayOffDto>>

    @Transaction
    @Query("SELECT * FROM day_off ORDER BY startYear, finishYear, startDay, startMonth, finishDay, finishMonth")
    suspend fun findAll(): List<DayOffDto>

    @Transaction
    @Query("SELECT * FROM day_off WHERE id = :id")
    fun findByIdInLiveData(id: Long): LiveData<DayOffDto>

    @Transaction
    @Query("SELECT * FROM day_off WHERE id = :id")
    suspend fun findById(id: Long): DayOffDto

    @Transaction
    @Query("SELECT * FROM day_off WHERE :year BETWEEN startYear AND finishYear AND :month BETWEEN startMonth AND finishMonth AND :day BETWEEN startDay AND finishDay")
    suspend fun findByDate(year: Int, month: Month?, day: Int): DayOffDto?

    @Insert
    override suspend fun insert(entity: DayOffDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: DayOffDto)

    @Delete
    override suspend fun delete(entity: DayOffDto)
}