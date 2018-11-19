package net.wojteksz128.worktimemeasureapp.util.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import net.wojteksz128.worktimemeasureapp.job.AppJob;

public abstract class JobUtils {

    private static final String LOG = JobUtils.class.getSimpleName();

    public static Job scheduleJob(@NonNull final Context context, AppJob appJob) {

        final GooglePlayDriver driver = new GooglePlayDriver(context);
        final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        final Job job = dispatcher.newJobBuilder()
                .setService(appJob.getJobServiceClass())
                .setTag(appJob.getTag())
                .setLifetime(appJob.getLifetime())
                .setTrigger(Trigger.executionWindow(appJob.getInterval(), appJob.getInterval() + appJob.getSyncFlexTime()))
                .setReplaceCurrent(appJob.isReplaceCurrent())
                .build();
        dispatcher.schedule(job);
        Log.i(LOG, "scheduleJob: " + appJob.getTag() + " job scheduled");
        return job;
    }
}
