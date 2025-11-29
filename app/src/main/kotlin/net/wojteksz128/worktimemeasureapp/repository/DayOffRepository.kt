package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.model.DayOff
import org.threeten.bp.LocalDate

open class DayOffRepository(
    private val dayOffDao: DayOffDao,
    dayOffMapper: DayOffMapper,
    historyService: HistoryService,
    historyDao: EntityHistoryDao,
) : Repository<DayOff, DayOffDto>(dayOffDao, dayOffMapper, historyService, historyDao) {

    override suspend fun getById(id: Long): DayOffDto? = dayOffDao.findById(id)

    open suspend fun getDayOff(date: LocalDate): DayOff? {
        val entity = dayOffDao.findByDate(date)
        return entity?.let { mapper.mapToDomainModel(it) }
    }

    open suspend fun getSimilarDaysOff(dayOff: DayOff): Collection<DayOff> =
        dayOffDao.findAllInDateRange(dayOff.startDate, dayOff.finishDate)
            .map { mapper.mapToDomainModel(it) }

    open fun getAllInLiveData(): LiveData<List<DayOff>> =
        dayOffDao.findAllInLiveData().map { dayOffDtoList ->
            dayOffDtoList.map { mapper.mapToDomainModel(it) }
        }

    open suspend fun getAll(): List<DayOff> =
        dayOffDao.findAll().map { mapper.mapToDomainModel(it) }
}
