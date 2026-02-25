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
import kotlinx.coroutines.flow.Flow
import net.wojteksz128.worktimemeasureapp.database.EntityDao
import org.threeten.bp.LocalDate

private const val FIND_ALL_QUERY = "SELECT * FROM work_day ORDER BY date DESC"
private const val FIND_BY_ID_QUERY = "SELECT * FROM work_day WHERE id = :id"
private const val FIND_BY_DATE_QUERY = "SELECT * FROM work_day WHERE date = :date"
private const val FIND_BETWEEN_DATES_QUERY =
    "SELECT * FROM work_day WHERE date BETWEEN :beginDate AND :endDate"
private const val DELETE_ALL_QUERY = "DELETE FROM work_day"


@Dao
@Suppress("unused")
interface WorkDayDao : EntityDao<WorkDayDto> {

    @Transaction
    @Query(FIND_ALL_QUERY)
    fun findAllInLiveData(): DataSource.Factory<Int, WorkDayWithEventsDto>

    @Transaction
    @Query(FIND_ALL_QUERY)
    suspend fun findAll(): List<WorkDayWithEventsDto>

    @Transaction
    @Query(FIND_BY_ID_QUERY)
    fun findByIdInLiveData(id: Int): LiveData<WorkDayWithEventsDto>

    @Transaction
    @Query(FIND_BY_ID_QUERY)
    suspend fun findById(id: Int): WorkDayWithEventsDto

    @Query(FIND_BY_ID_QUERY)
    suspend fun findByIdOrNull(id: Int): WorkDayDto?

    @Transaction
    @Query(FIND_BY_DATE_QUERY)
    suspend fun findByDate(date: LocalDate): WorkDayWithEventsDto?

    @Transaction
    @Query(FIND_BY_DATE_QUERY)
    fun findByDateInLiveData(date: LocalDate): LiveData<WorkDayWithEventsDto?>

    @Transaction
    @Query(FIND_BY_DATE_QUERY)
    fun findByDateAsFlow(date: LocalDate): Flow<WorkDayWithEventsDto?>

    @Transaction
    @Query(FIND_BETWEEN_DATES_QUERY)
    suspend fun findBetweenDates(
        beginDate: LocalDate,
        endDate: LocalDate,
    ): List<WorkDayWithEventsDto>

    @Transaction
    @Query(FIND_BETWEEN_DATES_QUERY)
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

    @Query(DELETE_ALL_QUERY)
    suspend fun deleteAll()
}
