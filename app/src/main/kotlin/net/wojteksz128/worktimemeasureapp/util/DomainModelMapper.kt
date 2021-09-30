package net.wojteksz128.worktimemeasureapp.util

interface DomainModelMapper<DomainModel, Entity> {

    fun mapFromDomainModel(domainModel: DomainModel): Entity

    fun mapToDomainModel(entity: Entity): DomainModel
}