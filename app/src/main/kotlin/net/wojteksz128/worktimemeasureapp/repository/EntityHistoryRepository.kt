package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.window.history.FieldChange
import net.wojteksz128.worktimemeasureapp.window.history.GroupedHistoryItem
import javax.inject.Inject

class EntityHistoryRepository @Inject constructor(
    private val entityHistoryDao: EntityHistoryDao,
) {

    fun getGroupedHistory(workDayId: Long): LiveData<List<GroupedHistoryItem>> {
        val rawHistory = entityHistoryDao.findHistoryForWorkDay(workDayId)

        return rawHistory.map { historyList ->
            historyList.groupBy { it.changeGroupId }
                .map { (_, group) ->
                    val first = group.first()
                    GroupedHistoryItem(
                        timestamp = first.timestamp,
                        entityType = first.entityType,
                        actionType = first.actionType,
                        changes = group.map {
                            FieldChange(
                                it.fieldName,
                                it.oldValue,
                                it.newValue
                            )
                        }
                    )
                }.sortedByDescending { it.timestamp }
        }
    }
}