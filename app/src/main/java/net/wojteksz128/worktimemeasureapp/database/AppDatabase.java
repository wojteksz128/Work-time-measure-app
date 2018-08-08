package net.wojteksz128.worktimemeasureapp.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {ComeEvent.class, WorkDay.class}, version = 3)
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
                        .addMigrations(getDatabaseMigrations())
                        .build();
            }
        }
        Log.d(LOG_TAG, "getInstance: Getting the database instance");
        return sInstance;
    }

    private static Migration[] getDatabaseMigrations() {
        List<Migration> migrations = new ArrayList<>();

        // Version 1 -> 2
        migrations.add(new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("CREATE TABLE work_day " +
                        "( id INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ", date INTEGER" +
                        ", worktime INTEGER" +
                        ", percentDeclaredTime REAL" +
                        ")");

                // TODO Zabezpiecz przed zapisem zadania
                database.execSQL("DELETE FROM come_event");

                database.execSQL("ALTER TABLE come_event ADD COLUMN workDayId INTEGER NOT NULL");
            }
        });

        // Version 2 -> 3
        migrations.add(new Migration(2, 3) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("DELETE FROM work_day");
                database.execSQL("DELETE FROM come_event");

                database.execSQL("ALTER TABLE work_day ADD COLUMN beginSlot INTEGER");
                database.execSQL("ALTER TABLE work_day ADD COLUMN endSlot INTEGER");
            }
        });


        return migrations.toArray(new Migration[0]);
    }

    public abstract ComeEventDao comeEventDao();

    public abstract WorkDayDao workDayDao();
}
