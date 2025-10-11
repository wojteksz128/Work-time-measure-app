package net.wojteksz128.worktimemeasureapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.database.history.EntityHistoryDao
import net.wojteksz128.worktimemeasureapp.database.history.HistoryService
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayMapper
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayWithEventsMapper
import net.wojteksz128.worktimemeasureapp.model.WorkDay
import net.wojteksz128.worktimemeasureapp.util.datetime.LocalDateRange
import org.threeten.bp.LocalDate

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

    suspend fun getWorkDayByDate(currentDate: LocalDate): WorkDay? {
        val entity = workDayDao.findByDate(currentDate)
        return entity?.let { workDayWithEventsMapper.mapToDomainModel(entity) }
    }

    // TODO: 09.10.2021 Czy oddzielne metody LiveData i normalne jest potrzebne?
    fun getWorkDayByDateInLiveData(date: LocalDate): LiveData<WorkDay> =
        workDayDao.findByDateInLiveData(date).map { workDayWithEventsDto ->
                workDayWithEventsDto?.let {
                    workDayWithEventsMapper.mapToDomainModel(it)
                } ?: WorkDay(date)
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

    suspend fun getWorkDaysForRange(dateRange: LocalDateRange): List<WorkDay> =
        withContext(Dispatchers.IO) {
            workDayDao.findBetweenDates(dateRange.start, dateRange.endInclusive).map {
                workDayWithEventsMapper.mapToDomainModel(it)
            }
        }

    fun getWorkDayByDateAsFlow(date: LocalDate): Flow<WorkDay?> =
        workDayDao.findByDateAsFlow(date).map { workDayWithEventsDto ->
            workDayWithEventsDto?.let {
                workDayWithEventsMapper.mapToDomainModel(it)
            }
        }
}