package net.wojteksz128.worktimemeasureapp.database.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import net.wojteksz128.worktimemeasureapp.database.EntityDao

@Dao
interface EntityHistoryDao : EntityDao<EntityHistoryDto> {

    @Insert
    override suspend fun insert(entity: EntityHistoryDto)

    // Not needed for history
    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(entity: EntityHistoryDto)

    // Not needed for history
    @Delete
    override suspend fun delete(entity: EntityHistoryDto)
}