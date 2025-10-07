package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDto
import javax.inject.Inject

class EntityHistoryRepository @Inject constructor(
    private val entityHistoryDao: EntityHistoryDao,
) {

    fun getGroupedHistory(workDayId: Long): LiveData<List<EntityHistoryDto>> =
        entityHistoryDao.findHistoryForWorkDay(workDayId)
}