package net.wojteksz128.worktimemeasureapp.database.dayOff

import androidx.lifecycle.LiveData
import androidx.room.*
import net.wojteksz128.worktimemeasureapp.database.EntityDao

@Dao
interface DayOffDao : EntityDao<DayOffDto> {

    @Transaction
    @Query("SELECT * FROM day_off ORDER BY startYear, finishYear, startDay, startMonth, finishDay, finishMonth")
    fun findAllInLiveData(): LiveData<List<DayOffDto>>

    @Transaction
    @Query("SELECT * FROM day_off ORDER BY startYear, finishYear, startDay, startMonth, finishDay, finishMonth")
    fun findAll(): List<DayOffDto>

    @Transaction
    @Query("SELECT * FROM day_off WHERE id = :id")
    fun findByIdInLiveData(id: Long): LiveData<DayOffDto>

    @Transaction
    @Query("SELECT * FROM day_off WHERE id = :id")
    fun findById(id: Long): DayOffDto

    @Insert
    override fun insert(entity: DayOffDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override fun update(entity: DayOffDto)

    @Delete
    override fun delete(entity: DayOffDto)
}