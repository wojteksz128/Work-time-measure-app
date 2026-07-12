package net.wojteksz128.worktimemeasureapp.window.history

import android.content.Context
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.formatToString
import net.wojteksz128.worktimemeasureapp.util.datetime.toCounterString
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.duration
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.isEnded
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.isEndingOnTheSameDay

sealed class ComeEventItemUiModel {
    abstract val id: Long
    abstract val startDateLabel: String
    abstract val originalEntity: ComeEvent

    data class Finished(
        override val id: Long,
        override val startDateLabel: String,
        val finishDateLabel: String,
        val formattedDuration: String,
        override val originalEntity: ComeEvent,
    ) : ComeEventItemUiModel()

    data class Active(
        override val id: Long,
        override val startDateLabel: String,
        override val originalEntity: ComeEvent,
    ) : ComeEventItemUiModel()
}

fun ComeEvent.toUiModel(context: Context): ComeEventItemUiModel {
    val formatPattern = context.getString(
        if (this.isEndingOnTheSameDay) {
        R.string.history_day_event_time_short_format
    } else {
        R.string.history_day_event_time_long_format
        }
    )

    val startDateLabel = this.startDate.formatToString(formatPattern)

    return if (this.isEnded) {
        ComeEventItemUiModel.Finished(
            id = this.id ?: 0L,
            startDateLabel = startDateLabel,
            finishDateLabel = this.endDate.formatToString(formatPattern),
            formattedDuration = this.duration.toCounterString(),
            originalEntity = this
        )
    } else {
        ComeEventItemUiModel.Active(
            id = this.id ?: 0L,
            startDateLabel = startDateLabel,
            originalEntity = this
        )
    }
}