package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.model.DayOff
import org.threeten.bp.ZonedDateTime

class DayOffRepository(
    private val dayOffDao: DayOffDao,
    dayOffMapper: DayOffMapper,
    historyService: HistoryService,
    historyDao: EntityHistoryDao,
) : Repository<DayOff, DayOffDto>(dayOffDao, dayOffMapper, historyService, historyDao) {

    override suspend fun getById(id: Long): DayOffDto? = dayOffDao.findById(id)

    suspend fun getDayOff(date: ZonedDateTime): DayOff? {
        val localDate = date.toLocalDate()!!
        val entity = dayOffDao.findByDate(localDate)
        return entity?.let { mapper.mapToDomainModel(it) }
    }

    suspend fun getSimilarDaysOff(dayOff: DayOff): Collection<DayOff> =
        dayOffDao.findAllInDateRange(dayOff.startDate, dayOff.finishDate)
            .map { mapper.mapToDomainModel(it) }

    fun getAllInLiveData(): LiveData<List<DayOff>> =
        dayOffDao.findAllInLiveData().map { dayOffDtoList ->
            dayOffDtoList.map { mapper.mapToDomainModel(it) }
        }

    suspend fun getAll(): List<DayOff> =
        dayOffDao.findAll().map { mapper.mapToDomainModel(it) }
}
