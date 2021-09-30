package net.wojteksz128.worktimemeasureapp.database.comeEvent

import net.wojteksz128.worktimemeasureapp.model.ComeEvent
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import javax.inject.Inject
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent as ComeEventDto

class ComeEventMapper @Inject constructor() : DomainModelMapper<ComeEvent, ComeEventDto> {

    override fun mapFromDomainModel(domainModel: ComeEvent): ComeEventDto =
        ComeEventDto(
            id = domainModel.id,
            startDate = domainModel.startDate,
            endDate = domainModel.endDate,
            durationLong = domainModel.durationMillis,
            workDayId = domainModel.workDayId
        )

    override fun mapToDomainModel(entity: ComeEventDto): ComeEvent =
        ComeEvent(
            id = entity.id,
            startDate = entity.startDate,
            endDate = entity.endDate,
            durationMillis = entity.durationLong,
            workDayId = entity.workDayId
        )

    fun mapFromDomainModelList(domainModels: List<ComeEvent>): List<ComeEventDto> =
        domainModels.map { mapFromDomainModel(it) }

    fun mapToDomainModelList(entities: List<ComeEventDto>): List<ComeEvent> =
        entities.map { mapToDomainModel(it) }
}