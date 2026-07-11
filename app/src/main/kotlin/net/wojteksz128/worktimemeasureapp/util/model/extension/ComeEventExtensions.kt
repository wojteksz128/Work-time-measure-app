package net.wojteksz128.worktimemeasureapp.util.model.extension

import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.isTheSameDay
import org.threeten.bp.Duration

object ComeEventExtensions {

    lateinit var dateTimeProvider: DateTimeProvider

    fun init(dateTimeProvider: DateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider
    }

    val ComeEvent.isEnded: Boolean
        get() = this.endDate != null

    val ComeEvent.duration: Duration
        get() = Duration.between(this.startDate, this.endDate ?: dateTimeProvider.currentTime)

    val ComeEvent.isEndingOnTheSameDay: Boolean
        get() = this.startDate.isTheSameDay(this.endDate ?: dateTimeProvider.currentTime)
}
