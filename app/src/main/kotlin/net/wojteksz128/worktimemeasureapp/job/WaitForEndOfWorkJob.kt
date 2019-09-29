package net.wojteksz128.worktimemeasureapp.job

import android.content.Context
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.firebase.jobdispatcher.Lifetime
import net.wojteksz128.worktimemeasureapp.configuration.ConfigurationProvider
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.notification.EndOfWorkNotification
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils
import org.joda.time.Duration

class WaitForEndOfWorkJob : JobService() {

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        EndOfWorkNotification(this).notifyUser()
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean {
        jobFinished(job, false)
        return true
    }

    companion object {
        const val tag = "end_of_work_reminder_tag"
        const val lifetime = Lifetime.FOREVER
        const val syncFlexTime = 15
        const val replaceCurrent = true

        fun getInterval(context: Context) {
            val currentWorkDay = AppDatabase.getInstance(context).workDayDao().findByIntervalContains(DateTimeProvider.currentTime)
            ConfigurationProvider.workTimeDuration.standardSeconds - Duration(DateTimeUtils.mergeComeEventsDuration(currentWorkDay).time).standardSeconds
        }
    }
}
