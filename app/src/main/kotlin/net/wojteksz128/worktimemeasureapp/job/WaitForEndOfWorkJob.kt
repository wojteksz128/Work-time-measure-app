package net.wojteksz128.worktimemeasureapp.job

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.firebase.jobdispatcher.*
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.notification.EndOfWorkNotification
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils
import org.joda.time.Duration
import java.text.MessageFormat

class WaitForEndOfWorkJob : JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        EndOfWorkNotification(this).notifyUser()
        jobFinished(jobParameters, false)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        jobFinished(jobParameters, false)
        return true
    }

    companion object {
        private val LOG = WaitForEndOfWorkJob::class.java.simpleName
        const val tag = "end_of_work_reminder_tag"
        const val lifetime = Lifetime.FOREVER
        const val syncFlexTime = 15
        const val replaceCurrent = true

        fun getInterval(context: Context): Int {
            val currentWorkDay = AppDatabase.getInstance(context).workDayDao()
                .findByIntervalContains(DateTimeProvider.currentTime)

            val interval =
                Settings.Work.Duration.getValue(context)?.standardSeconds?.toInt()
                    ?: 0 - Duration(DateTimeUtils.mergeComeEventsDuration(currentWorkDay).time).standardSeconds.toInt()
            return if (interval > 0) interval else 0
        }

        fun schedule(context: Context): AsyncTask<Context, Unit, Job> {
            return object : AsyncTask<Context, Unit, Job>() {
                override fun doInBackground(vararg p0: Context?): Job {
                    val jobServiceClass = WaitForEndOfWorkJob::class.java
                    val interval = getInterval(context)
                    val windowEnd = interval + syncFlexTime
                    val replaceCurrent = replaceCurrent

                    val driver = GooglePlayDriver(context)
                    val dispatcher = FirebaseJobDispatcher(driver)
                    val job = dispatcher.newJobBuilder()
                            .setService(jobServiceClass)
                            .setTag(tag)
                            .setLifetime(lifetime)
                            .setTrigger(Trigger.executionWindow(interval, windowEnd))
                            .setReplaceCurrent(replaceCurrent)
                            .build()
                    Log.d(LOG, MessageFormat.format("scheduleJob:\n" +
                            "Create job:\n" +
                            "\tservice class: {0},\n" +
                            "\ttag: {1},\n" +
                            "\tlifetime: {2},\n" +
                            "\texecution window: {3} - {4},\n" +
                            "\treplace current: {5}", jobServiceClass, tag, lifetime, interval,
                            windowEnd, replaceCurrent))
                    dispatcher.schedule(job)
                    Log.i(LOG, "scheduleJob: $tag job scheduled")
                    return job
                }

            }.execute(context)
        }
    }
}
