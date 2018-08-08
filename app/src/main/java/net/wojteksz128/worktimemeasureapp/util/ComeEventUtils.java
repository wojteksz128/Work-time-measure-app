package net.wojteksz128.worktimemeasureapp.util;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType;

import java.util.Date;
import java.util.List;

public class ComeEventUtils {


    public static void registerNewEvent(Context context, final Function<Void, Void> preFunction, final Function<ComeEventType, Void> postFunction) {
        final ComeEventDao eventDao = AppDatabase.getInstance(context).comeEventDao();
        final Date registerDate = new Date();

        new AsyncTask<Void, Void, ComeEventType>() {

            @Override
            protected void onPreExecute() {
                preFunction.apply(null);
            }

            @Override
            protected ComeEventType doInBackground(Void... voids) {
                List<ComeEvent> comeEvents = eventDao.findAll();
                ComeEvent comeEvent = comeEvents != null && !comeEvents.isEmpty() ? comeEvents.get(0) : null;
                ComeEventType comeEventType;

                if (comeEvent != null) {
                    comeEventType = comeEvent.getType().equals(ComeEventType.COME_IN) ? ComeEventType.COME_OUT : ComeEventType.COME_IN;
                } else {
                    comeEventType = ComeEventType.COME_IN;
                }
                eventDao.insert(new ComeEvent(registerDate, comeEventType, 0));

                return comeEventType;
            }

            @Override
            protected void onPostExecute(ComeEventType comeEventType) {
                postFunction.apply(comeEventType);
            }
        }.execute();
    }
}
