package net.wojteksz128.worktimemeasureapp.repository

import net.wojteksz128.worktimemeasureapp.database.EntityDao
import net.wojteksz128.worktimemeasureapp.database.EntityDto
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.model.DomainModel
import net.wojteksz128.worktimemeasureapp.util.DomainModelMapper
import net.wojteksz128.worktimemeasureapp.validation.DataValidator
import java.util.UUID

abstract class Repository<DM, E>(
    protected val dao: EntityDao<E>,
    protected val mapper: DomainModelMapper<DM, E>,
    private val historyService: HistoryService,
    private val historyDao: EntityHistoryDao,
    private val validator: DataValidator<DM>,
) where DM : DomainModel, E : EntityDto {

    abstract suspend fun getById(id: Long): E?

    open suspend fun save(domainModel: DM) {
        val validationResult = validator.validate(domainModel)
        if (!validationResult.isValid)
            throw DataValidationException(validationResult.errors)

        val newEntity = mapper.mapFromDomainModel(domainModel)
        val oldEntity = if (newEntity.id == null) null else getById(newEntity.id!!)

        val action = if (oldEntity == null) "INSERT" else "UPDATE"

        val storiedEntity = if (action == "INSERT") {
            val newId = dao.insert(newEntity)
            getById(newId)
        } else {
            dao.update(newEntity)
            newEntity
        }

        addToHistory(oldEntity, storiedEntity, action)
    }

    open suspend fun delete(domainModel: DM) {
        val entityToDelete = mapper.mapFromDomainModel(domainModel)
        dao.delete(entityToDelete)

        addToHistory(entityToDelete, null, "DELETE")
    }

    private suspend fun addToHistory(oldEntity: E?, newEntity: E?, action: String) {
        val changeGroupId = UUID.randomUUID().toString()
        val changes = historyService.getChanges(oldEntity, newEntity, action, changeGroupId)
        changes.forEach { historyDao.insert(it) }
    }
}

class DataValidationException(val errors: List<String>) :
    Exception("Data validation error: ${errors.joinToString()}")