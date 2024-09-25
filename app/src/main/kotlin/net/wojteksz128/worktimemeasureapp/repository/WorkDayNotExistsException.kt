package net.wojteksz128.worktimemeasureapp.repository

import org.threeten.bp.ZonedDateTime

class WorkDayNotExistsException(currentDate: ZonedDateTime) :
    Exception("Work day for $currentDate not exists")
