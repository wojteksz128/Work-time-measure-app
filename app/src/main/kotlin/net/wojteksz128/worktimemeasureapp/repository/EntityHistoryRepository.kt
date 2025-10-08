package net.wojteksz128.worktimemeasureapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.window.history.FieldChange
import net.wojteksz128.worktimemeasureapp.window.history.GroupedHistoryItem
import net.wojteksz128.worktimemeasureapp.window.history.formatters.HistoryFormatterProvider
import net.wojteksz128.worktimemeasureapp.window.history.formatters.HistoryValueFormatter
import javax.inject.Inject

private val Any?.isPrimitive: Boolean
    get() = when (this) {
        is String, is Number, is Boolean -> true
        else -> false
    }

class EntityHistoryRepository @Inject constructor(
    private val entityHistoryDao: EntityHistoryDao,
    private val gson: Gson,
    private val formatterProvider: HistoryFormatterProvider,
) : ClassTagAware {

    fun getGroupedHistoryForWorkDay(workDayId: Long): LiveData<List<GroupedHistoryItem>> {
        val rawHistory = entityHistoryDao.findHistoryForWorkDay(workDayId)

        return rawHistory.map { historyList ->
            historyList.groupBy { it.changeGroupId }
                .mapNotNull { (_, group) ->
                    val first = group.first()
                    val formatter = formatterProvider.getFormatter(first.entityType)

                    val changes = if (first.actionType == "DELETE" && first.fieldName == "entity")
                        parseDeletedEntity(first.oldValue, formatter)
                    else group.mapNotNull { entry ->
                        val formattedOld = formatter.format(entry.fieldName, entry.oldValue)
                        val formattedNew = formatter.format(entry.fieldName, entry.newValue)
                        if (formattedOld != formattedNew) FieldChange(
                            entry.fieldName,
                            formattedOld,
                            formattedNew
                        )
                        else null
                    }
                    if (changes.isEmpty()) null
                    else
                        GroupedHistoryItem(
                            timestamp = first.timestamp,
                            entityType = first.entityType,
                            actionType = first.actionType,
                            changes = changes
                        )
                }.sortedByDescending { it.timestamp }
        }
    }

    private fun parseDeletedEntity(
        json: String?,
        formatter: HistoryValueFormatter,
    ): List<FieldChange> {
        if (json == null) return emptyList()

        return try {
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            val entityMap = gson.fromJson<Map<String, Any?>>(json, type)

            entityMap.mapNotNull { (key, value) ->
                val valueForFormatting =
                    value?.let { if (it.isPrimitive) it.toString() else gson.toJson(it) }
                val formattedValue = formatter.format(key, valueForFormatting)
                if (formattedValue != null) {
                    FieldChange(key, formattedValue, null)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.w(classTag, "parseDeletedEntity: error parsing deleted entity", e)
            listOf(FieldChange("entity", json, null))
        }

    }
}