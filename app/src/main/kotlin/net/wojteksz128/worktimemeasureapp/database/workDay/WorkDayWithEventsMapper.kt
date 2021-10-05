package net.wojteksz128.worktimemeasureapp.database.workDay

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import javax.inject.Inject

class WorkDayWithEventsMapper @Inject constructor(
    private val workDayMapper: WorkDayMapper,
    private val comeEventMapper: ComeEventMapper
) : DomainModelMapper<WorkDay, WorkDayWithEventsDto> {

    override fun mapFromDomainModel(domainModel: WorkDay): WorkDayWithEventsDto {
        return WorkDayWithEventsDto(
            workDayMapper.mapFromDomainModel(domainModel),
            comeEventMapper.mapFromDomainModelList(domainModel.events)
        )
    }

    override fun mapToDomainModel(entity: WorkDayWithEventsDto): WorkDay {
        return WorkDay(
            id = entity.workDay.id,
            date = entity.workDay.date,
            beginSlot = entity.workDay.beginSlot,
            endSlot = entity.workDay.endSlot,
            events = comeEventMapper.mapToDomainModelList(entity.events).toMutableList()
        )
    }

    fun mapFromDomainModelList(domainModels: List<WorkDay>): List<WorkDayWithEventsDto> {
        return domainModels.map { mapFromDomainModel(it) }
    }

    fun mapToDomainModelList(entities: List<WorkDayWithEventsDto>): List<WorkDay> {
        return entities.map { mapToDomainModel(it) }
    }
}