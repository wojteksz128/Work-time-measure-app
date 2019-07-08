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