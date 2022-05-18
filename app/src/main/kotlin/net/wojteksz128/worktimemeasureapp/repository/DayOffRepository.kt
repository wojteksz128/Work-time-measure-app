package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.model.DayOff

class DayOffRepository(
    dayOffDao: DayOffDao,
    dayOffMapper: DayOffMapper
) : Repository<DayOff, DayOffDto>(dayOffDao, dayOffMapper)
