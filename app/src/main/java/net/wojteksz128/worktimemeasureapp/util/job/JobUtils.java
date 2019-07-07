package net.wojteksz128.worktimemeasureapp.util.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Trigger;

import net.wojteksz128.worktimemeasureapp.job.AppJob;

import java.text.MessageFormat;

public abstract class JobUtils {

    private static final String LOG = JobUtils.class.getSimpleName();

    public static Job scheduleJob(@NonNull final Context context, AppJob appJob) {

        final Class<? extends JobService> jobServiceClass = appJob.jobServiceClass;
        final String tag = appJob.tag;
        final int lifetime = appJob.lifetime;
        final int interval = appJob.getInterval(context);
        final int windowEnd = interval + appJob.syncFlexTime;
        final boolean replaceCurrent = appJob.replaceCurrent;

        final GooglePlayDriver driver = new GooglePlayDriver(context);
        final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        final Job job = dispatcher.newJobBuilder()
                .setService(jobServiceClass)
                .setTag(tag)
                .setLifetime(lifetime)
                .setTrigger(Trigger.executionWindow(interval, windowEnd))
                .setReplaceCurrent(replaceCurrent)
                .build();
        Log.d(LOG, MessageFormat.format("scheduleJob:\n" +
                        "Create job:\n" +
                        "\tservice class: {0},\n" +
                        "\ttag: {1},\n" +
                        "\tlifetime: {2},\n" +
                        "\texecution window: {3} - {4},\n" +
                        "\treplace current: {5}", jobServiceClass, tag, lifetime, interval,
                        windowEnd, replaceCurrent));
        dispatcher.schedule(job);
        Log.i(LOG, "scheduleJob: " + tag + " job scheduled");
        return job;
    }
}
