package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffMapper
import net.wojteksz128.worktimemeasureapp.model.DayOff
import org.threeten.bp.Month
import java.util.*

class DayOffRepository(
    private val dayOffDao: DayOffDao,
    dayOffMapper: DayOffMapper,
) : Repository<DayOff, DayOffDto>(dayOffDao, dayOffMapper) {

    suspend fun getDayOff(date: Date): DayOff? {
        val calendar = Calendar.getInstance().apply { time = date }
        val year = calendar.get(Calendar.YEAR)
        val month = Month.of(calendar.get(Calendar.MONTH) + 1)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val entity = dayOffDao.findByDate(year, month, day)
        return entity?.let { mapper.mapToDomainModel(it) }
    }

    fun getAllInLiveData(): LiveData<List<DayOff>> =
        dayOffDao.findAllInLiveData().map { dayOffDtoList ->
            dayOffDtoList.map { mapper.mapToDomainModel(it) }
        }
}
