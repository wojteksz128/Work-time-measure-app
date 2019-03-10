package net.wojteksz128.worktimemeasureapp.job;

import android.arch.core.util.Function;
import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils;

import java.util.concurrent.TimeUnit;

public enum AppJob {
    // TODO: 18.11.2018 Make it more flexible - calculate required time.
    END_OF_WORK_REMINDER(EndOfWorkFirebaseJobService.class,
            "end_of_work_reminder_tag",
            Lifetime.FOREVER,
            new Function<Context, Integer>() {

                @Override
                public Integer apply(Context context) {
                    final WorkDayEvents currentWorkDay = AppDatabase.getInstance(context).workDayDao().findCurrentWorkDay();
                    return (int)((TimeUnit.HOURS.toSeconds(8) + TimeUnit.MINUTES.toSeconds(30)) - TimeUnit.MILLISECONDS.toSeconds(DateTimeUtils.mergeComeEventsDuration(currentWorkDay).getTime()));
                }
            },
            15,
            true);



    private static final String LOG = AppJob.class.getSimpleName();
    private static final int EMPTY_INTERVAL = -1;

    public final Class<? extends JobService> jobServiceClass;
    public final String tag;
    public final int lifetime;
    private final int interval;
    private final Function<Context, Integer> intervalCalculator;
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

    AppJob(Class<? extends JobService> jobServiceClass, String tag, int lifetime, Function<Context, Integer> intervalCalculator, int syncFlexTime, boolean replaceCurrent) {

        this.jobServiceClass = jobServiceClass;
        this.tag = tag;
        this.lifetime = lifetime;
        this.interval = EMPTY_INTERVAL;
        this.intervalCalculator = intervalCalculator;
        this.syncFlexTime = syncFlexTime;
        this.replaceCurrent = replaceCurrent;
    }

    public int getInterval(final Context context) {
        try {
            return this.interval != EMPTY_INTERVAL
                    ? this.interval
                    : this.intervalCalculator != null ? Math.max(this.intervalCalculator.apply(context), 0) : 0;
        } catch (Exception e) {
            Log.e(LOG, e.getLocalizedMessage());
            return 0;
        }
    }
}
