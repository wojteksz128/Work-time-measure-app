package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import net.wojteksz128.worktimemeasureapp.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryDisplayMapper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun mapActionType(action: String): String = when (action) {
        "DELETE" -> context.getString(R.string.work_day_history_action_type_deleted)
        "INSERT" -> context.getString(R.string.work_day_history_action_type_added)
        "UPDATE" -> context.getString(R.string.work_day_history_action_type_modified)
        else -> action
    }

    fun mapActionToColor(action: String): Int = when (action) {
        "DELETE" -> R.color.history_deleted
        "INSERT" -> R.color.history_added
        "UPDATE" -> R.color.history_modified
        else -> R.color.design_default_color_on_secondary
    }

    fun mapEntityType(entityType: String): String = when (entityType) {
        "ComeEventDto" -> context.getString(R.string.work_day_history_entity_type_come_event)
        "WorkDayDto" -> context.getString(R.string.work_day_history_entity_type_work_day)
        else -> entityType
    }

    fun mapFieldName(fieldName: String): String {
        @StringRes val resId = when (fieldName) {
            "startDate" -> R.string.work_day_history_change_field_name_startDate
            "endDate" -> R.string.work_day_history_change_field_name_endDate
            "type" -> R.string.work_day_history_change_field_name_type
            "beginSlot" -> R.string.work_day_history_change_field_name_beginSlot
            "endSlot" -> R.string.work_day_history_change_field_name_endSlot
            "date" -> R.string.work_day_history_change_field_name_date
            else -> return fieldName
        }
        return context.getString(resId)
    }
}