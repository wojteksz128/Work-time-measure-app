package net.wojteksz128.worktimemeasureapp.database.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "entity_history")
data class EntityHistoryDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Long? = null,

    @ColumnInfo(name = "changeGroupId")
    val changeGroupId: String,

    @ColumnInfo(name = "entityType")
    val entityType: String,

    @ColumnInfo(name = "entityId")
    val entityId: Long,

    @ColumnInfo(name = "actionType")
    val actionType: String,

    @ColumnInfo(name = "fieldName")
    val fieldName: String,

    @ColumnInfo(name = "oldValue")
    val oldValue: String?,

    @ColumnInfo(name = "newValue")
    val newValue: String?,

    @ColumnInfo(name = "timestamp")
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
) : EntityDto