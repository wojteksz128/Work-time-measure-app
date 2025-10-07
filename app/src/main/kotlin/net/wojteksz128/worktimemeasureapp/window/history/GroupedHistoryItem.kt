package net.wojteksz128.worktimemeasureapp.window.history

import org.threeten.bp.ZonedDateTime

data class GroupedHistoryItem(
    val timestamp: ZonedDateTime,
    val entityType: String,
    val actionType: String,
    val changes: List<FieldChange>,
)

data class FieldChange(
    val fieldName: String,
    val oldValue: String?,
    val newValue: String?,
)