package net.wojteksz128.worktimemeasureapp.database.history

import com.google.gson.Gson
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import javax.inject.Inject
import kotlin.reflect.full.memberProperties


class HistoryService @Inject constructor(private val gson: Gson) {

    fun <T : EntityDto> getChanges(
        oldEntity: T?,
        newEntity: T?,
        action: String,
    ): List<EntityHistoryDto> {
        val changes = mutableListOf<EntityHistoryDto>()
        val entityType = (oldEntity ?: newEntity)!!::class.java.simpleName
        val entityId = (oldEntity ?: newEntity)!!.id!!

        when (action) {
            "INSERT" -> {
                newEntity!!::class.memberProperties.forEach { property ->
                    changes.add(
                        EntityHistoryDto(
                            entityType = entityType,
                            entityId = entityId,
                            actionType = "INSERT",
                            fieldName = property.name,
                            oldValue = null,
                            newValue = property.getter.call(newEntity)?.toString()
                        )
                    )
                }
            }

            "UPDATE" -> {
                oldEntity!!::class.memberProperties.forEach { property ->
                    val oldValue = property.getter.call(oldEntity)?.toString()
                    val newValue = property.getter.call(newEntity)?.toString()
                    if (oldValue != newValue) {
                        changes.add(
                            EntityHistoryDto(
                                entityType = entityType,
                                entityId = entityId,
                                actionType = "UPDATE",
                                fieldName = property.name,
                                oldValue = oldValue,
                                newValue = newValue
                            )
                        )
                    }
                }
            }

            "DELETE" -> {
                changes.add(
                    EntityHistoryDto(
                        entityType = entityType,
                        entityId = entityId,
                        actionType = "DELETE",
                        fieldName = "entity",
                        oldValue = gson.toJson(oldEntity),
                        newValue = null
                    )
                )
            }
        }

        return changes
    }
}