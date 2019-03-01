package net.wojteksz128.worktimemeasureapp.util.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import net.wojteksz128.worktimemeasureapp.job.AppJob;

import java.text.MessageFormat;

public abstract class JobUtils {

    private static final String LOG = JobUtils.class.getSimpleName();

    public static Job scheduleJob(@NonNull final Context context, AppJob appJob) {

        final GooglePlayDriver driver = new GooglePlayDriver(context);
        final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        final Job job = dispatcher.newJobBuilder()
                .setService(appJob.jobServiceClass)
                .setTag(appJob.tag)
                .setLifetime(appJob.lifetime)
                .setTrigger(Trigger.executionWindow(appJob.getInterval(), appJob.getInterval() + appJob.syncFlexTime))
                .setReplaceCurrent(appJob.replaceCurrent)
                .build();
        Log.d(LOG, MessageFormat.format("Create job:\n\tservice class: {0},\n\ttag: {1},\n" +
                "\tlifetime: {2},\n\texecution window: {3} - {4},\n\treplace current: {5}",
                appJob.jobServiceClass, appJob.tag, appJob.lifetime, appJob.getInterval(),
                appJob.getInterval() + appJob.syncFlexTime, appJob.replaceCurrent));
        dispatcher.schedule(job);
        Log.i(LOG, "scheduleJob: " + appJob.tag + " job scheduled");
        return job;
    }
}
