package net.wojteksz128.worktimemeasureapp.job

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import net.wojteksz128.worktimemeasureapp.notification.endOfWork.EndOfWorkNotification

class EndOfWorkFirebaseJobService : JobService() {

    private var mBackgroundTask: AsyncTask<Unit, Unit, Unit>? = null

    @SuppressLint("StaticFieldLeak")
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        mBackgroundTask = object : AsyncTask<Unit, Unit, Unit>() {

            override fun doInBackground(vararg p0: Unit?) {
                val context = this@EndOfWorkFirebaseJobService
                EndOfWorkNotification.createNotification(context)
            }

            override fun onPostExecute(result: Unit?) {
                jobFinished(jobParameters, false)
            }
        }

        mBackgroundTask!!.execute()

        return true
    }

    override fun onStopJob(job: JobParameters): Boolean {
        if (mBackgroundTask != null) {
            mBackgroundTask!!.cancel(true)
        }

        return true
    }
}
