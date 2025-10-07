package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.window.history.FieldChange
import net.wojteksz128.worktimemeasureapp.window.history.GroupedHistoryItem
import javax.inject.Inject

class EntityHistoryRepository @Inject constructor(
    private val entityHistoryDao: EntityHistoryDao,
    private val gson: Gson,
) {

    fun getGroupedHistory(workDayId: Long): LiveData<List<GroupedHistoryItem>> {
        val rawHistory = entityHistoryDao.findHistoryForWorkDay(workDayId)

        return rawHistory.map { historyList ->
            historyList.groupBy { it.changeGroupId }
                .map { (_, group) ->
                    val first = group.first()
                    val changes = if (first.actionType == "DELETE" && first.fieldName == "entity")
                        parseDeletedEntity(first.oldValue)
                    else
                        group.map { FieldChange(it.fieldName, it.oldValue, it.newValue) }
                    GroupedHistoryItem(
                        timestamp = first.timestamp,
                        entityType = first.entityType,
                        actionType = first.actionType,
                        changes = changes
                    )
                }.sortedByDescending { it.timestamp }
        }
    }

    private fun parseDeletedEntity(json: String?): List<FieldChange> {
        if (json == null) return emptyList()

        return try {
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            val entityMap = gson.fromJson<Map<String, Any?>>(json, type)

            entityMap.map { (key, value) ->
                FieldChange(key, value?.toString(), null)
            }
        } catch (_: Exception) {
            listOf(FieldChange("entity", json, null))
        }

    }
}