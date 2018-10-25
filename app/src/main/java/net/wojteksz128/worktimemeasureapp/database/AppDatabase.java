package net.wojteksz128.worktimemeasureapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom1To2;
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom2To3;
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom3To4;
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom4To5;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;

import java.util.Arrays;

@Database(entities = {ComeEvent.class, WorkDay.class}, version = 5)
@TypeConverters({DatabaseConverters.DateConverter.class, DatabaseConverters.ComeEventTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_FILENAME =  "work-time-measure.db";
    private static final String TAG = AppDatabase.class.getSimpleName();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "getInstance: Creating new database instance");
                final String filePath = context.getExternalFilesDir(null).getAbsolutePath() + "/" + AppDatabase.DATABASE_FILENAME;
                Log.d(TAG, "getInstance: Database path: " + filePath);
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, filePath)
                        .addMigrations(getDatabaseMigrations())
                        .build();
            }
        }
        Log.d(LOG_TAG, "getInstance: Getting the database instance");
        return sInstance;
    }

    private static Migration[] getDatabaseMigrations() {
        return Arrays.asList(
                new MigrateFrom1To2(),      // Version 1    ->      2
                new MigrateFrom2To3(),      // Version 2    ->      3
                new MigrateFrom3To4(),      // Version 3    ->      4
                new MigrateFrom4To5()       // Version 4    ->      5
        ).toArray(new Migration[0]);
    }

    public abstract ComeEventDao comeEventDao();

    public abstract WorkDayDao workDayDao();
}
