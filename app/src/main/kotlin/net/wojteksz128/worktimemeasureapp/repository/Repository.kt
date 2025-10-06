package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.EntityDao
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.model.DomainModel
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper

abstract class Repository<DM, E>(
    protected val dao: EntityDao<E>,
    protected val mapper: DomainModelMapper<DM, E>,
    private val historyService: HistoryService,
    private val historyDao: EntityHistoryDao,
) where DM : DomainModel, E : EntityDto {

    abstract suspend fun getById(id: Long): E?

    open suspend fun save(domainModel: DM) {
        val newEntity = mapper.mapFromDomainModel(domainModel)
        val oldEntity = if (newEntity.id == null) null else getById(newEntity.id!!)

        val action = if (oldEntity == null) "INSERT" else "UPDATE"

        if (action == "INSERT")
            dao.insert(newEntity)
        else
            dao.update(newEntity)

        addToHistory(oldEntity, newEntity, action)
    }

    open suspend fun delete(domainModel: DM) {
        val entityToDelete = mapper.mapFromDomainModel(domainModel)
        dao.delete(entityToDelete)

        addToHistory(entityToDelete, null, "DELETE")
    }

    private suspend fun addToHistory(oldEntity: E?, newEntity: E?, action: String) {
        val changes = historyService.getChanges(oldEntity, newEntity, action)
        changes.forEach { historyDao.insert(it) }
    }
}