package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.formatToString
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import net.wojteksz128.worktimemeasureapp.util.model.extension.isWorkFinished
import net.wojteksz128.worktimemeasureapp.util.model.extension.workTime

sealed class WorkDayItemUiModel {
    abstract val id: Long
    abstract val dateLabel: String
    abstract val events: List<ComeEventItemUiModel>
    abstract val hasEvents: Boolean
    abstract val originalEntity: WorkDay

    data class Finished(
        override val id: Long,
        override val dateLabel: String,
        override val events: List<ComeEventItemUiModel>,
        override val hasEvents: Boolean,
        override val originalEntity: WorkDay,
        val formattedDuration: String,
    ) : WorkDayItemUiModel()

    data class Active(
        override val id: Long,
        override val dateLabel: String,
        override val events: List<ComeEventItemUiModel>,
        override val hasEvents: Boolean,
        override val originalEntity: WorkDay,
    ) : WorkDayItemUiModel()
}

fun WorkDay.toUiModel(context: Context): WorkDayItemUiModel {
    val id = this.id ?: throw IllegalStateException("WorkDay must have an ID")
    val dateLabelPattern = context.getString(R.string.history_work_day_label_format)
    val dateLabel = this.date.formatToString(dateLabelPattern)
    val hasEvents = this.events.isNotEmpty()
    val uiEvents = this.events.map { it.toUiModel(context) }

    return if (this.isWorkFinished) {
        WorkDayItemUiModel.Finished(
            id = id,
            dateLabel = dateLabel,
            events = uiEvents,
            hasEvents = hasEvents,
            originalEntity = this,
            formattedDuration = this.workTime.toCounterString()
        )
    } else {
        WorkDayItemUiModel.Active(
            id = id,
            dateLabel = dateLabel,
            events = uiEvents,
            hasEvents = hasEvents,
            originalEntity = this
        )
    }
}