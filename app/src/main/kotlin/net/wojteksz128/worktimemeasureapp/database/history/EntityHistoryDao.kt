package net.wojteksz128.worktimemeasureapp.database.history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.wojteksz128.worktimemeasureapp.database.EntityDao

@Dao
interface EntityHistoryDao : EntityDao<EntityHistoryDto> {

    @Query(
        """
        SELECT * FROM entity_history
        WHERE
            (entityType = 'WorkDayDto' AND entityId = :workDayId)
            OR (entityType = 'ComeEventDto' AND entityId IN (SELECT id FROM come_event WHERE workDayId = :workDayId))
            OR (entityType = 'ComeEventDto' AND actionType = 'DELETE' AND json_extract(oldValue, '$.workDayId') = :workDayId)
        ORDER BY timestamp DESC
    """
    )
    fun findHistoryForWorkDay(workDayId: Long): LiveData<List<EntityHistoryDto>>

    @Insert
    override suspend fun insert(entity: EntityHistoryDto): Long

    // Not needed for history
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: EntityHistoryDto)

    // Not needed for history
    @Delete
    override suspend fun delete(entity: EntityHistoryDto)
}