package net.wojteksz128.worktimemeasureapp.job

import android.content.Context
import android.util.Log
import com.firebase.jobdispatcher.JobService
import com.firebase.jobdispatcher.Lifetime
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import java.util.concurrent.TimeUnit
import kotlin.math.max

enum class AppJob {

    END_OF_WORK_REMINDER(WaitForEndOfWorkJob::class.java,
            "end_of_work_reminder_tag",
            Lifetime.FOREVER,
            { context ->
                val currentWorkDay = AppDatabase.getInstance(context).workDayDao().findByIntervalContains(DateTimeProvider.currentTime)
                (TimeUnit.HOURS.toSeconds(8) + TimeUnit.MINUTES.toSeconds(30) - TimeUnit.MILLISECONDS.toSeconds(DateTimeUtils.mergeComeEventsDuration(currentWorkDay).time)).toInt()
            },
            15,
            true);

    private val LOG = AppJob::class.java.simpleName
    private val EMPTY_INTERVAL = -1

    val jobServiceClass: Class<out JobService>
    val tag: String
    val lifetime: Int
    private val interval: Int
    private val intervalCalculator: ((Context) -> Int)?
    val syncFlexTime: Int
    val replaceCurrent: Boolean

    constructor(jobServiceClass: Class<out JobService>, tag: String, lifetime: Int, interval: Int, syncFlexTime: Int, replaceCurrent: Boolean) {
        this.jobServiceClass = jobServiceClass
        this.tag = tag
        this.lifetime = lifetime
        this.interval = interval
        this.intervalCalculator = null
        this.syncFlexTime = syncFlexTime
        this.replaceCurrent = replaceCurrent
    }

    constructor(jobServiceClass: Class<out JobService>, tag: String, lifetime: Int, intervalCalculator: (Context) -> Int, syncFlexTime: Int, replaceCurrent: Boolean) {
        this.jobServiceClass = jobServiceClass
        this.tag = tag
        this.lifetime = lifetime
        this.interval = EMPTY_INTERVAL
        this.intervalCalculator = intervalCalculator
        this.syncFlexTime = syncFlexTime
        this.replaceCurrent = replaceCurrent
    }

    fun getInterval(context: Context): Int {
        return try {
            if (this.interval != EMPTY_INTERVAL) this.interval
            else max(intervalCalculator?.invoke(context) ?: 0, 0)
        } catch (e: Exception) {
            Log.e(LOG, e.localizedMessage)
            0
        }

    }
}
