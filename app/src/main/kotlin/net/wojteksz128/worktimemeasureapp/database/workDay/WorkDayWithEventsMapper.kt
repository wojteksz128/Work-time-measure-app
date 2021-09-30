package net.wojteksz128.worktimemeasureapp.database.workDay

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import javax.inject.Inject

class WorkDayWithEventsMapper @Inject constructor(
    private val workDayMapper: WorkDayMapper,
    private val comeEventMapper: ComeEventMapper
) : DomainModelMapper<WorkDay, WorkDayEvents> {

    override fun mapFromDomainModel(domainModel: WorkDay): WorkDayEvents {
        return WorkDayEvents(
            workDayMapper.mapFromDomainModel(domainModel),
            comeEventMapper.mapFromDomainModelList(domainModel.events)
        )
    }

    override fun mapToDomainModel(entity: WorkDayEvents): WorkDay {
        return WorkDay(
            id = entity.workDay.id,
            date = entity.workDay.date,
            beginSlot = entity.workDay.beginSlot,
            endSlot = entity.workDay.endSlot,
            events = comeEventMapper.mapToDomainModelList(entity.events).toMutableList()
        )
    }

    fun mapFromDomainModelList(domainModels: List<WorkDay>): List<WorkDayEvents> {
        return domainModels.map { mapFromDomainModel(it) }
    }

    fun mapToDomainModelList(entities: List<WorkDayEvents>): List<WorkDay> {
        return entities.map { mapToDomainModel(it) }
    }
}