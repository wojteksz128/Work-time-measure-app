package net.wojteksz128.worktimemeasureapp.job

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import net.wojteksz128.worktimemeasureapp.notification.endOfWork.EndOfWorkNotification

class EndOfWorkFirebaseJobService : JobService() {

    private var mBackgroundTask: AsyncTask<Void, Void, Void>? = null

    @SuppressLint("StaticFieldLeak")
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        mBackgroundTask = object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(objects: Array<Void>): Void? {
                val context = this@EndOfWorkFirebaseJobService
                EndOfWorkNotification.createNotification(context)
                return null
            }

            override fun onPostExecute(o: Void) {
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
