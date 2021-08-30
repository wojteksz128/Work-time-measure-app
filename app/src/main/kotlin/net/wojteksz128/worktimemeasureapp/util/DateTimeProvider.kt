package net.wojteksz128.worktimemeasureapp.util

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.medavox.library.mutime.MuTime
import net.wojteksz128.worktimemeasureapp.settings.Settings
import java.util.*

object DateTimeProvider {
    private var offset: Long = 0
    private val TAG = DateTimeProvider::class.java.simpleName

    val currentTime: Date
        get() = Date(System.currentTimeMillis() + offset)

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
            val firstWeekDay = Settings.WorkTime.FirstWeekDay.valueNullable
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
                        Log.e(TAG, "Failed to get the actual time. Take old offset.", e)
                        offset
                    }
                }

                override fun onPostExecute(result: Long) {
                    Log.d(TAG, "Change offset from $offset to $result.")
                    offset = result
                    super.onPostExecute(result)
                }
            }.execute()
        }
    }
}