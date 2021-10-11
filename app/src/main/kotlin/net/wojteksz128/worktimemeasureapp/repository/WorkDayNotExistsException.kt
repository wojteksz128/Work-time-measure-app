package net.wojteksz128.worktimemeasureapp.repository

import java.util.*

class WorkDayNotExistsException(currentDate: Date) :
    Exception("Work day for $currentDate not exists")
