package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.model.ComeEvent

class ComeEventRepository(
    private val comeEventDao: ComeEventDao,
    private val comeEventMapper: ComeEventMapper) {

    fun save(comeEvent: ComeEvent) {
        val comeEventDto = comeEventMapper.mapFromDomainModel(comeEvent)
        if (comeEventDto.id == null) {
            comeEventDao.insert(comeEventDto)
        } else {
            comeEventDao.update(comeEventDto)
        }
    }
}
