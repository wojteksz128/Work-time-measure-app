package net.wojteksz128.worktimemeasureapp.util

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.medavox.library.mutime.MuTime
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
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            return c.time
        }
    val weekBeginDay: Date
        get() {
            val c = Calendar.getInstance()
            c.time = currentTime
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.clear(Calendar.MINUTE)
            c.clear(Calendar.SECOND)
            c.clear(Calendar.MILLISECOND)
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            return c.time
        }

    fun updateOffset(context: Context, ntpHost: String) {
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