package net.wojteksz128.worktimemeasureapp.database.workDay

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import java.util.*

@Dao
@Suppress("unused")
interface WorkDayDao {

    @Transaction
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    fun findAllInLiveData(): LiveData<List<WorkDayEvents>>

    @Transaction
    @Query("SELECT * FROM work_day ORDER BY date DESC")
    fun findAll(): List<WorkDayEvents>

    @Transaction
    @Query("SELECT * FROM work_day WHERE id = :id")
    fun findByIdInLiveData(id: Int): LiveData<WorkDayEvents>

    @Transaction
    @Query("SELECT * FROM work_day WHERE id = :id")
    fun findById(id: Int): WorkDayEvents

    @Transaction
    @Query("SELECT * FROM work_day WHERE :date BETWEEN beginSlot AND endSlot")
    fun findByIntervalContains(date: Date): WorkDayEvents

    @Insert
    fun insert(workDay: WorkDay): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(workDay: WorkDay)

    @Delete
    fun delete(workDay: WorkDay)
}
