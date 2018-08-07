package net.wojteksz128.worktimemeasureapp;

import android.content.Context;
import android.os.AsyncTask;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.ComeEventType;

import java.util.Date;
import java.util.List;

public class ComeEventExecutor {


    public static void registerNewEvent(Context context) {
        final ComeEventDao eventDao = AppDatabase.getInstance(context).comeEventDao();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<ComeEvent> comeEvents = eventDao.findAll();
                ComeEvent comeEvent = comeEvents != null ? comeEvents.get(0) : null;
                if (comeEvent != null) {
                    ComeEventType comeEventType = comeEvent.getType().equals(ComeEventType.COME_IN) ? ComeEventType.COME_OUT : ComeEventType.COME_IN;
                    eventDao.insert(new ComeEvent(new Date(), comeEventType));
                }
                return null;
            }
        }.execute();
    }
}
