package net.wojteksz128.worktimemeasureapp.database.dayOff

import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import javax.inject.Inject

class DayOffMapper @Inject constructor() : DomainModelMapper<DayOff, DayOffDto> {

    override fun mapFromDomainModel(domainModel: DayOff) =
        DayOffDto(
            id = domainModel.id,
            uuid = domainModel.uuid,
            type = domainModel.type,
            name = domainModel.name,
            startDate = domainModel.startDate,
            finishDate = domainModel.finishDate,
            source = domainModel.source
        )

    override fun mapToDomainModel(entity: DayOffDto) =
        DayOff(
            id = entity.id,
            uuid = entity.uuid,
            type = entity.type,
            name = entity.name,
            startDate = entity.startDate,
            finishDate = entity.finishDate,
            source = entity.source
        )

}
