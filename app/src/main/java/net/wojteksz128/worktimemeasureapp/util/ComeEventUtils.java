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

    public static void registerNewEvent(Context context, final Function<Void, Void> preFunction, final Function<ComeEventType, Void> postFunction) {
        final ComeEventDao comeEventDao = AppDatabase.getInstance(context).comeEventDao();
        final WorkDayDao workDayDao = AppDatabase.getInstance(context).workDayDao();
        final Date registerDate = new Date();

        new AsyncTask<Void, Void, ComeEventType>() {

            @Override
            protected void onPreExecute() {
                preFunction.apply(null);
            }

            @Override
            protected ComeEventType doInBackground(Void... voids) {
                WorkDayEvents workDay = workDayDao.findByIntervalContains(registerDate);
                ComeEventType comeEventType;

                if (workDay == null) {
                    workDay = createNewWorkDay(registerDate, workDayDao);
                }

                final ComeEvent comeEvent;
                if (workDay.getEvents() == null || workDay.getEvents().isEmpty()) {
                    comeEventType = ComeEventType.COME_IN;
                    comeEvent = new ComeEvent(registerDate, workDay.getWorkDay());
                    comeEventDao.insert(comeEvent);
                } else {
                    final ComeEvent tmpEvent = workDay.getEvents().get(0);
                    if (tmpEvent.getEndDate() != null) {
                        comeEvent = new ComeEvent(registerDate, workDay.getWorkDay());
                        comeEventDao.insert(comeEvent);

                        comeEventType = ComeEventType.COME_IN;
                    } else {
                        comeEvent = tmpEvent;
                        comeEvent.setEndDate(registerDate);
                        comeEvent.setDuration(calculateDuration(comeEvent));
                        comeEventDao.update(comeEvent);

                        comeEventType = ComeEventType.COME_OUT;
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
        workDay.setWorkDay(new WorkDay(insertedWorkdayId.intValue(),
                workDay.getWorkDay().getDate(), workDay.getWorkDay().getBeginSlot(),
                workDay.getWorkDay().getEndSlot(), workDay.getWorkDay().getWorktime(),
                workDay.getWorkDay().getPercentDeclaredTime()));
        return workDay;
    }
}
