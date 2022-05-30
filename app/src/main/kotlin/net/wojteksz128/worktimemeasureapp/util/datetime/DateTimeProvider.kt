package net.wojteksz128.worktimemeasureapp.util.datetime

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.medavox.library.mutime.MuTime
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.util.*
import javax.inject.Inject

class DateTimeProvider @Inject constructor(
    private val Settings: Settings
) : ClassTagAware {
    private var offset: Long = 0

    val currentTime: Date
        get() = Date(System.currentTimeMillis() + offset)

    val currentCalendar: Calendar
        get() = currentCalendarWithoutCorrection.apply { time = currentTime }

    val currentCalendarWithoutCorrection: Calendar
        get() = Calendar.getInstance()

    val weekEndDay: Date
        get() {
            val c = Calendar.getInstance()
            c.time = weekBeginDay
            c.add(Calendar.WEEK_OF_YEAR, 1)
            c.add(Calendar.MILLISECOND, -1)
            return c.time
        }

    val weekBeginDay: Date
        get() {
            val firstWeekDay = Settings.WorkTime.Week.FirstWeekDay.valueNullable
            val c = Calendar.getInstance()
            val currentTime = currentTime
            c.time = currentTime
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.clear(Calendar.MINUTE)
            c.clear(Calendar.SECOND)
            c.clear(Calendar.MILLISECOND)
            c.set(Calendar.DAY_OF_WEEK, firstWeekDay ?: Calendar.MONDAY)
            if (c.time > currentTime)
                c.add(Calendar.WEEK_OF_YEAR, -1)

            return c.time
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