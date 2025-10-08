package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class WorkDayRepository (
    private val workDayDao: WorkDayDao,
    workDayMapper: WorkDayMapper,
    private val workDayWithEventsMapper: WorkDayWithEventsMapper,
    historyService: HistoryService,
    historyDao: EntityHistoryDao,
) : Repository<WorkDay, WorkDayDto>(workDayDao, workDayMapper, historyService, historyDao) {

    override suspend fun getById(id: Long): WorkDayDto? = workDayDao.findById(id.toInt()).workDay

    // TODO: 09.10.2021 Key z Int do Long
    fun getAllPaged(): () -> PagingSource<Int, WorkDay> =
        workDayDao.findAllInLiveData()
            .mapByPage { workDayWithEventsMapper.mapToDomainModelList(it) }
            .asPagingSourceFactory(Dispatchers.IO)

    suspend fun getCurrentWorkDay(currentDate: ZonedDateTime): WorkDay? {
        val entity = workDayDao.findByIntervalContains(currentDate)
        return entity?.let { workDayWithEventsMapper.mapToDomainModel(entity) }
    }

    // TODO: 09.10.2021 Czy oddzielne metody LiveData i normalne jest potrzebne?
    fun getCurrentWorkDayInLiveData(currentDate: ZonedDateTime): LiveData<WorkDay?> =
        workDayDao.findByIntervalContainsInLiveData(currentDate)
            .map { workDayWithEventsDto ->
                workDayWithEventsDto?.let {
                    workDayWithEventsMapper.mapToDomainModel(it)
                }
            }

    suspend fun getWorkDayById(workDayId: Long): WorkDay? =
        workDayDao.findById(workDayId.toInt()).let { workDayWithEventsDto ->
            workDayWithEventsMapper.mapToDomainModel(workDayWithEventsDto)
        }

    fun getWorkDayByIdInLiveData(workDayId: Long): LiveData<WorkDay?> =
        workDayDao.findByIdInLiveData(workDayId.toInt()).map { workDayWithEventsDto ->
            workDayWithEventsDto.let {
                workDayWithEventsMapper.mapToDomainModel(it)
            }
        }

    fun getCurrentWeekWorkDaysInLiveData(
        start: LocalDate,
        end: LocalDate,
    ): LiveData<List<WorkDay>> =
        workDayDao.findBetweenDates(start, end)
            .map { workDayWithEventsMapper.mapToDomainModelList(it) }
}