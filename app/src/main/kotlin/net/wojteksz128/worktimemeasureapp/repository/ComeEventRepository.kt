package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.model.ComeEvent

open class ComeEventRepository(
    private val comeEventDao: ComeEventDao,
    comeEventMapper: ComeEventMapper,
    historyService: HistoryService,
    historyDao: EntityHistoryDao,
) : Repository<ComeEvent, ComeEventDto>(comeEventDao, comeEventMapper, historyService, historyDao) {

    override suspend fun getById(id: Long): ComeEventDto? = comeEventDao.findById(id.toInt())
}
