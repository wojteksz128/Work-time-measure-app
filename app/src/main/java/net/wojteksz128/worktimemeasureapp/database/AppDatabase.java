package net.wojteksz128.worktimemeasureapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao;

@Database(entities = {ComeEvent.class}, version = 1, exportSchema = false)
@TypeConverters({DatabaseConverters.DateConverter.class, DatabaseConverters.ComeEventTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "work-time-measure";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "getInstance: Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, AppDatabase.DATABASE_NAME)
                    .build();
            }
        }
        Log.d(LOG_TAG, "getInstance: Getting the database instance");
        return sInstance;
    }

    public abstract ComeEventDao comeEventDao();
}
