package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.EntityDao
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.model.DomainModel
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper

abstract class Repository<DM, E>(
    protected val dao: EntityDao<E>,
    protected val mapper: DomainModelMapper<DM, E>
) where DM : DomainModel, E : EntityDto {

    open suspend fun save(domainModel: DM) {
        val entity = mapper.mapFromDomainModel(domainModel)
        if (entity.id == null)
            dao.insert(entity)
        else
            dao.update(entity)
    }

    open suspend fun delete(domainModel: DM) {
        val entity = mapper.mapFromDomainModel(domainModel)
        dao.delete(entity)
    }
}