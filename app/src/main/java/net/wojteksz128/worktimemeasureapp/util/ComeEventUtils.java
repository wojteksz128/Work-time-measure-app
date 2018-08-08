package net.wojteksz128.worktimemeasureapp.util;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;

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
                    workDay = new WorkDayEvents();
                    workDay.setWorkDay(new WorkDay(registerDate));
                    workDay.setEvents(new ArrayList<ComeEvent>());
                    workDayDao.insert(workDay.getWorkDay());
                    comeEventType = ComeEventType.COME_IN;

                } else {
                    if (workDay.getEvents() == null || workDay.getEvents().isEmpty()) {
                        comeEventType = ComeEventType.COME_IN;
                    } else {
                        final ComeEventType type = workDay.getEvents().get(0).getType();
                        comeEventType = type.equals(ComeEventType.COME_IN) ? ComeEventType.COME_OUT : ComeEventType.COME_IN;
                    }
                }

                final ComeEvent comeEvent = new ComeEvent(registerDate, comeEventType, workDay.getWorkDay());
                comeEventDao.insert(comeEvent);

                return comeEventType;
            }

            @Override
            protected void onPostExecute(ComeEventType comeEventType) {
                postFunction.apply(comeEventType);
            }
        }.execute();
    }
}
