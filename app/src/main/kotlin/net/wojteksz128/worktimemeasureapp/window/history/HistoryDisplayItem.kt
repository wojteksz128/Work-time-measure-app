package net.wojteksz128.worktimemeasureapp.window.history

import androidx.annotation.ColorRes

data class HistoryDisplayItem(
    val timestamp: String,
    val actionText: String,
    @ColorRes val actionColorRes: Int,
    val entityText: String,
    val changes: List<ChangeDisplayItem>,
)

data class ChangeDisplayItem(
    val fieldName: String,
    val oldValue: String?,
    val newValue: String?,
)