package net.wojteksz128.worktimemeasureapp.util;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;

import java.util.ArrayList;
import java.util.Date;

public class ComeEventUtils {

    public static void registerNewEvent(final Context context, final Function<Void, Void> preFunction, final Function<ComeEventType, Void> postFunction) {
        final ComeEventDao comeEventDao = AppDatabase.getInstance(context).comeEventDao();
        final Date registerDate = new Date();

        new AsyncTask<Void, Void, ComeEventType>() {

            @Override
            protected void onPreExecute() {
                preFunction.apply(null);
            }

            @Override
            protected ComeEventType doInBackground(Void... voids) {
                ComeEventType comeEventType;
                WorkDayEvents workDay = getCurrentWorkDay(registerDate, context);

                if (isFirstWorkDayEvent(workDay)) {
                    comeEventType = createNewEvent(workDay, registerDate, comeEventDao);
                } else {
                    final ComeEvent comeEvent = workDay.getEvents().get(0);
                    if (comeEvent.getEndDate() != null) {
                        comeEventType = createNewEvent(workDay, registerDate, comeEventDao);
                    } else {
                        comeEventType = assignEndDateIntoCurrentEvent(comeEvent, registerDate, comeEventDao);
                    }
                }

                return comeEventType;
            }

            @Override
            protected void onPostExecute(ComeEventType comeEventType) {
                postFunction.apply(comeEventType);
            }
        }.execute();
    }

    @NonNull
    private static ComeEventType assignEndDateIntoCurrentEvent(ComeEvent comeEvent, Date registerDate, ComeEventDao comeEventDao) {
        comeEvent.setEndDate(registerDate);
        comeEvent.setDuration(DateTimeUtils.calculateDuration(comeEvent));
        comeEventDao.update(comeEvent);
        return ComeEventType.COME_OUT;
    }

    @NonNull
    private static ComeEventType createNewEvent(WorkDayEvents workDay, Date registerDate, ComeEventDao comeEventDao) {
        comeEventDao.insert(new ComeEvent(registerDate, workDay.getWorkDay()));
        return ComeEventType.COME_IN;
    }

    private static boolean isFirstWorkDayEvent(WorkDayEvents workDay) {
        return workDay.getEvents() == null || workDay.getEvents().isEmpty();
    }

    @NonNull
    private static WorkDayEvents getCurrentWorkDay(Date registerDate, Context context) {
        final WorkDayDao workDayDao = AppDatabase.getInstance(context).workDayDao();
        WorkDayEvents workDay = workDayDao.findByIntervalContains(registerDate);

        if (workDay == null) {
            workDay = createNewWorkDay(registerDate, workDayDao);
        }
        return workDay;
    }

    @NonNull
    private static WorkDayEvents createNewWorkDay(Date registerDate, WorkDayDao workDayDao) {
        final WorkDayEvents workDay = new WorkDayEvents();

        workDay.setWorkDay(new WorkDay(registerDate));
        workDay.setEvents(new ArrayList<ComeEvent>());
        final Long insertedWorkdayId = workDayDao.insert(workDay.getWorkDay());
        workDay.setWorkDay(new WorkDay(insertedWorkdayId.intValue(), workDay.getWorkDay().getDate(),
                workDay.getWorkDay().getBeginSlot(), workDay.getWorkDay().getEndSlot(),
                workDay.getWorkDay().getPercentDeclaredTime()));
        return workDay;
    }
}
