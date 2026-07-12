package net.wojteksz128.worktimemeasureapp.util.model.extension

import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.duration
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions.isEnded
import org.threeten.bp.Duration

val WorkDay.finishedEventsDuration: Duration
    get() = this.events.filter { it.isEnded }.map { it.duration }
        .fold(Duration.ZERO) { sum, duration -> sum + duration }

val WorkDay.isNew: Boolean
    get() = this.events.isEmpty()

val WorkDay.isWorkFinished: Boolean
    get() = !this.isNew && this.events.all { it.isEnded }

val WorkDay.notEndedEvent: ComeEvent?
    get() = this.events.lastOrNull { !it.isEnded }

val WorkDay.workTime: Duration
    get() = this.events.duration