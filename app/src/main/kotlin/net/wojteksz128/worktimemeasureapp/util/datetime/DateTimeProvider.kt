package net.wojteksz128.worktimemeasureapp.util.datetime

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.medavox.library.mutime.MuTime
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class DateTimeProvider @Inject constructor(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
) : ClassTagAware {
    private var offset: Long = 0

    val currentTime: ZonedDateTime
        get() = ZonedDateTime.now(ZoneId.systemDefault()).plus(offset, ChronoUnit.MILLIS)

    val currentCalendar: Calendar
        get() = currentCalendarWithoutCorrection.apply { time = currentTime.toDate() }

    val currentCalendarWithoutCorrection: Calendar
        get() = Calendar.getInstance()

    val weekEndDay: LocalDate
        get() {
            val c = Calendar.getInstance()
            c.time =
                Date(weekBeginDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            c.add(Calendar.WEEK_OF_YEAR, 1)
            c.add(Calendar.MILLISECOND, -1)
            return Instant.ofEpochMilli(c.time.time).atZone(ZoneId.systemDefault()).toLocalDate()
        }

    val weekBeginDay: LocalDate
        get() {
            val firstWeekDay = Settings.WorkTime.Week.FirstWeekDay.valueNullable
            val c = Calendar.getInstance()
            val currentTime = currentTime
            c.time = currentTime.toDate()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.clear(Calendar.MINUTE)
            c.clear(Calendar.SECOND)
            c.clear(Calendar.MILLISECOND)
            c.set(Calendar.DAY_OF_WEEK, firstWeekDay ?: Calendar.MONDAY)
            if (c.time > currentTime.toDate())
                c.add(Calendar.WEEK_OF_YEAR, -1)

            return Instant.ofEpochMilli(c.time.time).atZone(ZoneId.systemDefault()).toLocalDate()
        }

    fun updateOffset(context: Context) {
        val timeSyncEnabled = Settings.Sync.TimeSync.Enabled.value
        if (timeSyncEnabled) {
            val ntpHost = Settings.Sync.TimeSync.ServerAddress.value
            object : AsyncTask<Unit, Unit, Long>() {
                override fun doInBackground(vararg p0: Unit?): Long {
                    return try {
                        MuTime.enableDiskCaching(context)
                        MuTime.requestTimeFromServer(ntpHost)
                        MuTime.now() - System.currentTimeMillis()
                    } catch (e: Throwable) {
                        Log.e(classTag, "Failed to get the actual time. Take old offset.", e)
                        offset
                    }
                }

                override fun onPostExecute(result: Long) {
                    Log.d(classTag, "Change offset from $offset to $result.")
                    offset = result
                    super.onPostExecute(result)
                }
            }.execute()
        }
    }
}