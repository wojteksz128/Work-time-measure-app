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
import java.util.Calendar;
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
        ComeEventType comeEventType;
        comeEvent.setEndDate(registerDate);
        comeEvent.setDuration(calculateDuration(comeEvent));
        comeEventDao.update(comeEvent);
        comeEventType = ComeEventType.COME_OUT;
        return comeEventType;
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

    private static Date calculateDuration(ComeEvent comeEvent) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(comeEvent.getEndDate().getTime() - comeEvent.getStartDate().getTime());
        return calendar.getTime();
    }

    @NonNull
    private static WorkDayEvents createNewWorkDay(Date registerDate, WorkDayDao workDayDao) {
        WorkDayEvents workDay;
        workDay = new WorkDayEvents();
        workDay.setWorkDay(new WorkDay(registerDate));
        workDay.setEvents(new ArrayList<ComeEvent>());
        final Long insertedWorkdayId = workDayDao.insert(workDay.getWorkDay());
        workDay.setWorkDay(new WorkDay(insertedWorkdayId.intValue(), workDay.getWorkDay().getDate(),
                workDay.getWorkDay().getBeginSlot(), workDay.getWorkDay().getEndSlot(),
                workDay.getWorkDay().getPercentDeclaredTime()));
        return workDay;
    }

    public static Date calculateSummaryDuration(WorkDayEvents workDay) {
        long millisSum = 0;

        for (ComeEvent comeEvent : workDay.getEvents()) {
            millisSum += comeEvent.getDuration() != null ? comeEvent.getDuration().getTime() : 0;
        }

        return new Date(millisSum);
    }
}
