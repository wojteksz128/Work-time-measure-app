package net.wojteksz128.worktimemeasureapp.job;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;

import java.util.concurrent.TimeUnit;

public enum AppJob {
    // TODO: 18.11.2018 Make it more flexible - calculate required time.
    END_OF_WORK_REMINDER(EndOfWorkFirebaseJobService.class,
            "end_of_work_reminder_tag",
            Lifetime.FOREVER,
            (int)(TimeUnit.HOURS.toSeconds(8) + TimeUnit.MINUTES.toSeconds(30)),
            15,
            true);


    private final Class<? extends JobService> jobServiceClass;
    private final String tag;
    private final int lifetime;
    private final int interval;
    private final int syncFlexTime;
    private final boolean replaceCurrent;

    AppJob(Class<? extends JobService> jobServiceClass, String tag, int lifetime, int interval, int syncFlexTime, boolean replaceCurrent) {

        this.jobServiceClass = jobServiceClass;
        this.tag = tag;
        this.lifetime = lifetime;
        this.interval = interval;
        this.syncFlexTime = syncFlexTime;
        this.replaceCurrent = replaceCurrent;
    }

    public Class<? extends JobService> getJobServiceClass() {
        return jobServiceClass;
    }

    public String getTag() {
        return tag;
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getInterval() {
        return interval;
    }

    public int getSyncFlexTime() {
        return syncFlexTime;
    }

    public boolean isReplaceCurrent() {
        return replaceCurrent;
    }
}
