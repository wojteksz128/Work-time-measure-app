package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import java.util.*

class WorkDayRepository (
    private val workDayDao: WorkDayDao,
    workDayMapper: WorkDayMapper,
    private val workDayWithEventsMapper: WorkDayWithEventsMapper
) : Repository<WorkDay, WorkDayDto>(workDayDao, workDayMapper) {

    // TODO: 09.10.2021 Key z Int do Long
    fun getAllPaged(): () -> PagingSource<Int, WorkDay> =
        workDayDao.findAllInLiveData()
            .mapByPage { workDayWithEventsMapper.mapToDomainModelList(it) }
            .asPagingSourceFactory(Dispatchers.IO)

    fun getCurrentWorkDay(currentDate: Date, createIfNotExists: Boolean = true): WorkDay {
        val entity = workDayDao.findByIntervalContains(currentDate)
        val workDay = entity?.let { workDayWithEventsMapper.mapToDomainModel(entity) }
            ?: if (createIfNotExists) {
                save(WorkDay(currentDate))
                return getCurrentWorkDay(currentDate, false)
            } else {
                throw WorkDayNotExistsException(currentDate)
            }

        return workDay
    }

    // TODO: 09.10.2021 Czy oddzielne metody LiveData i normalne jest potrzebne?
    fun getCurrentWorkDayInLiveData(currentDate: Date): LiveData<WorkDay> =
        workDayDao.findByIntervalContainsInLiveData(currentDate)
            .map { workDayWithEventsDto ->
                workDayWithEventsDto?.let {
                    workDayWithEventsMapper.mapToDomainModel(it)
                }
            }

    fun getCurrentWeekWorkDaysInLiveData(start: Date, end: Date): LiveData<List<WorkDay>> =
        workDayDao.findBetweenDates(start, end)
            .map { workDayWithEventsMapper.mapToDomainModelList(it) }
}