package net.wojteksz128.worktimemeasureapp;

import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.ComeEventType;

import java.util.Date;
import java.util.List;

public class ComeEventExecutor {


    public static void registerNewEvent(Context context, final Function<ComeEventType, Void> endFunction) {
        final ComeEventDao eventDao = AppDatabase.getInstance(context).comeEventDao();

        new AsyncTask<Void, Void, ComeEventType>() {
            @Override
            protected ComeEventType doInBackground(Void... voids) {
                List<ComeEvent> comeEvents = eventDao.findAll();
                ComeEvent comeEvent = comeEvents != null ? comeEvents.get(0) : null;
                ComeEventType comeEventType = null;

                if (comeEvent != null) {
                    comeEventType = comeEvent.getType().equals(ComeEventType.COME_IN) ? ComeEventType.COME_OUT : ComeEventType.COME_IN;
                    eventDao.insert(new ComeEvent(new Date(), comeEventType));
                }

                return comeEventType;
            }

            @Override
            protected void onPostExecute(ComeEventType comeEventType) {
                endFunction.apply(comeEventType);
            }
        }.execute();
    }
}
