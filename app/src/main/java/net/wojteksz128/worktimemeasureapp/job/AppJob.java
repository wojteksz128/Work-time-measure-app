package net.wojteksz128.worktimemeasureapp.job;

import android.util.Log;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public enum AppJob {
    // TODO: 18.11.2018 Make it more flexible - calculate required time.
    END_OF_WORK_REMINDER(EndOfWorkFirebaseJobService.class,
            "end_of_work_reminder_tag",
            Lifetime.FOREVER,
            (int)(TimeUnit.HOURS.toSeconds(8) + TimeUnit.MINUTES.toSeconds(30)),
            15,
            true);



    private static final String LOG = AppJob.class.getSimpleName();
    private static final int EMPTY_INTERVAL = -1;

    public final Class<? extends JobService> jobServiceClass;
    public final String tag;
    public final int lifetime;
    private final int interval;
    private final Callable<Integer> intervalCalculator;
    public final int syncFlexTime;
    public final boolean replaceCurrent;

    AppJob(Class<? extends JobService> jobServiceClass, String tag, int lifetime, int interval, int syncFlexTime, boolean replaceCurrent) {

        this.jobServiceClass = jobServiceClass;
        this.tag = tag;
        this.lifetime = lifetime;
        this.interval = interval;
        this.intervalCalculator = null;
        this.syncFlexTime = syncFlexTime;
        this.replaceCurrent = replaceCurrent;
    }

    AppJob(Class<? extends JobService> jobServiceClass, String tag, int lifetime, Callable<Integer> intervalCalculator, int syncFlexTime, boolean replaceCurrent) {

        this.jobServiceClass = jobServiceClass;
        this.tag = tag;
        this.lifetime = lifetime;
        this.interval = EMPTY_INTERVAL;
        this.intervalCalculator = intervalCalculator;
        this.syncFlexTime = syncFlexTime;
        this.replaceCurrent = replaceCurrent;
    }

    public int getInterval() {
        try {
            return this.interval != EMPTY_INTERVAL
                    ? this.interval
                    : this.intervalCalculator != null ? this.intervalCalculator.call() : 0;
        } catch (Exception e) {
            Log.e(LOG, e.getLocalizedMessage());
            return 0;
        }
    }
}
