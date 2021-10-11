package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.model.ComeEvent

class ComeEventRepository(
    comeEventDao: ComeEventDao,
    comeEventMapper: ComeEventMapper,
) : Repository<ComeEvent, ComeEventDto>(comeEventDao, comeEventMapper)
