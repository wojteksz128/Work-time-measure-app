package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.model.DayOff
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.util.Date

class DayOffRepository(
    private val dayOffDao: DayOffDao,
    dayOffMapper: DayOffMapper,
) : Repository<DayOff, DayOffDto>(dayOffDao, dayOffMapper) {

    suspend fun getDayOff(date: Date): DayOff? {
        val localDate =
            Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()!!
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
