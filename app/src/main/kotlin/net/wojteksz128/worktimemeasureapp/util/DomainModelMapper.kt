package net.wojteksz128.worktimemeasureapp.util

import net.wojteksz128.worktimemeasureapp.model.DomainModel

interface DomainModelMapper<DM, Entity>
where DM : DomainModel {

    fun mapFromDomainModel(domainModel: DM): Entity

    fun mapToDomainModel(entity: Entity): DM
}