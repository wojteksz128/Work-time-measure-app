package net.wojteksz128.worktimemeasureapp.util.job

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Job
import com.firebase.jobdispatcher.Trigger
import net.wojteksz128.worktimemeasureapp.job.AppJob
import java.text.MessageFormat

object JobUtils {

    private val LOG = JobUtils::class.java.simpleName

    fun scheduleJob(context: Context, appJob: AppJob): AsyncTask<Unit, Unit, Job>? {
        return object : AsyncTask<Unit, Unit, Job>() {
            override fun doInBackground(vararg p0: Unit?): Job {
                val jobServiceClass = appJob.jobServiceClass
                val tag = appJob.tag
                val lifetime = appJob.lifetime
                val interval = appJob.getInterval(context)
                val windowEnd = interval + appJob.syncFlexTime
                val replaceCurrent = appJob.replaceCurrent

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

        }.execute()
    }
}
