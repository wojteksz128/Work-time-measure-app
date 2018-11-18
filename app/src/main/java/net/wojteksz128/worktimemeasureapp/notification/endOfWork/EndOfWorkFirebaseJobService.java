package net.wojteksz128.worktimemeasureapp.notification.endOfWork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class EndOfWorkFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mBackgroundTask;


    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mBackgroundTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void[] objects) {
                Context context = EndOfWorkFirebaseJobService.this;
                EndOfWorkNotification.createNotification(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                jobFinished(jobParameters, false);
            }
        };

        mBackgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) {
            mBackgroundTask.cancel(true);
        }

        return true;
    }
}
