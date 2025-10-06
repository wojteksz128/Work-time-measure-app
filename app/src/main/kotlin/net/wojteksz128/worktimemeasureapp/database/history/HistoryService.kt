package net.wojteksz128.worktimemeasureapp.database.history

import net.wojteksz128.worktimemeasureapp.database.EntityDto
import kotlin.reflect.full.memberProperties


class HistoryService {

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
                        oldValue = oldEntity.toString(),
                        newValue = null
                    )
                )
            }
        }

        return changes
    }
}