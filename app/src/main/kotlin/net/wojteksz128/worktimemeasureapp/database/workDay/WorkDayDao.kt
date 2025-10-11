package net.wojteksz128.worktimemeasureapp.database.workDay

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import net.wojteksz128.worktimemeasureapp.database.EntityDao
import org.threeten.bp.LocalDate

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
    @Query("SELECT * FROM work_day WHERE date = :date")
    suspend fun findByDate(date: LocalDate): WorkDayWithEventsDto?

    @Transaction
    @Query("SELECT * FROM work_day WHERE date = :date")
    fun findByDateInLiveData(date: LocalDate): LiveData<WorkDayWithEventsDto?>

    @Transaction
    @Query("SELECT * FROM work_day WHERE date BETWEEN :beginDate AND :endDate")
    suspend fun findBetweenDates(
        beginDate: LocalDate,
        endDate: LocalDate,
    ): List<WorkDayWithEventsDto>

    @Transaction
    @Query("SELECT * FROM work_day WHERE date BETWEEN :beginDate AND :endDate")
    fun findBetweenDatesInLiveData(
        beginDate: LocalDate,
        endDate: LocalDate,
    ): LiveData<List<WorkDayWithEventsDto>>

    @Insert
    override suspend fun insert(entity: WorkDayDto): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: WorkDayDto)

    @Delete
    override suspend fun delete(entity: WorkDayDto)
}
