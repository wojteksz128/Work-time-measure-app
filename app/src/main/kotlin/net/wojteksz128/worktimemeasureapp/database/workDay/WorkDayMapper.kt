package net.wojteksz128.worktimemeasureapp.database.workDay

import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import javax.inject.Inject
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay as WorkDayDto

class WorkDayMapper @Inject constructor() : DomainModelMapper<WorkDay, WorkDayDto> {

    override fun mapFromDomainModel(domainModel: WorkDay) =
        WorkDayDto(
            id = domainModel.id,
            date = domainModel.date,
            beginSlot = domainModel.beginSlot,
            endSlot = domainModel.endSlot
        )

    override fun mapToDomainModel(entity: WorkDayDto) =
        WorkDay(
            id = entity.id,
            date = entity.date,
            beginSlot = entity.beginSlot,
            endSlot = entity.endSlot
        )

    fun mapFromDomainModelList(domainModels: List<WorkDay>): List<WorkDayDto> =
        domainModels.map { mapFromDomainModel(it) }

    fun mapToDomainModelList(entities: List<WorkDayDto>): List<WorkDay> =
        entities.map { mapToDomainModel(it) }
}