package net.wojteksz128.worktimemeasureapp;

import android.content.Context;
import android.os.AsyncTask;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.ComeEventType;

import java.util.Date;

public class ComeEventExecutor {


    public static void registerNewEvent(Context context) {
        final ComeEventDao eventDao = AppDatabase.getInstance(context).comeEventDao();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                eventDao.insert(new ComeEvent(new Date(), ComeEventType.COME_IN));
                return null;
            }
        }.execute();
    }
}
