package net.wojteksz128.worktimemeasureapp.database.dayOff

import net.wojteksz128.worktimemeasureapp.model.DayOff
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import javax.inject.Inject

class DayOffMapper @Inject constructor() : DomainModelMapper<DayOff, DayOffDto> {

    override fun mapFromDomainModel(domainModel: DayOff) =
        DayOffDto(
            id = domainModel.id,
            type = domainModel.type,
            startDay = domainModel.startDay,
            startMonth = domainModel.startMonth,
            startYear = domainModel.startYear,
            finishDay = domainModel.finishDay,
            finishMonth = domainModel.finishMonth,
            finishYear = domainModel.finishYear,
            source = domainModel.source
        )

    override fun mapToDomainModel(entity: DayOffDto) =
        DayOff(
            id = entity.id,
            type = entity.type,
            startDay = entity.startDay,
            startMonth = entity.startMonth,
            startYear = entity.startYear,
            finishDay = entity.finishDay,
            finishMonth = entity.finishMonth,
            finishYear = entity.finishYear,
            source = entity.source
        )

}
