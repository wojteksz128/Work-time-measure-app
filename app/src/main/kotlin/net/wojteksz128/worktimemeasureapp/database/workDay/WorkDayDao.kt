package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import net.wojteksz128.worktimemeasureapp.database.EntityDao
import java.util.*

@Dao
@Suppress("unused")
interface WorkDayDao : EntityDao<WorkDayDto> {

    @Transaction
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    fun findAllInLiveData(): DataSource.Factory<Int, WorkDayWithEventsDto>

    @Transaction
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    suspend fun findAll(): List<WorkDayWithEventsDto>

    @Transaction
    @Query("SELECT * FROM work_day WHERE id = :id")
    fun findByIdInLiveData(id: Int): LiveData<WorkDayWithEventsDto>

    @Transaction
    @Query("SELECT * FROM work_day WHERE id = :id")
    suspend fun findById(id: Int): WorkDayWithEventsDto

    @Transaction
    @Query("SELECT * FROM work_day WHERE :date BETWEEN beginSlot AND endSlot")
    suspend fun findByIntervalContains(date: Date): WorkDayWithEventsDto?

    @Transaction
    @Query("SELECT * FROM work_day WHERE :date BETWEEN beginSlot AND endSlot")
    fun findByIntervalContainsInLiveData(date: Date): LiveData<WorkDayWithEventsDto?>

    @Transaction
    @Query("SELECT * FROM work_day WHERE date BETWEEN :beginDate AND :endDate")
    fun findBetweenDates(beginDate: Date, endDate: Date): LiveData<List<WorkDayWithEventsDto>>

    @Insert
    override suspend fun insert(entity: WorkDayDto)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: WorkDayDto)

    @Delete
    override suspend fun delete(entity: WorkDayDto)
}
