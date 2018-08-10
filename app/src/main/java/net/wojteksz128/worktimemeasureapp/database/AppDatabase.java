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

@Database(entities = {ComeEvent.class, WorkDay.class}, version = 4)
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
                database.execSQL("DROP TABLE come_event");

                database.execSQL("CREATE TABLE IF NOT EXISTS `work_day` " +
                        "( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                        ", `date` INTEGER" +
                        ", `worktime` INTEGER" +
                        ", `percentDeclaredTime` REAL NOT NULL" +
                        ")");

                database.execSQL("CREATE TABLE IF NOT EXISTS come_event " +
                        "( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                        ", `date` INTEGER" +
                        ", `type` TEXT" +
                        ", `workDayId` INTEGER NOT NULL" +
                        ")");
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

        // Version 3 -> 4
        migrations.add(new Migration(3, 4) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("CREATE TABLE IF NOT EXISTS come_event_tmp (" +
                        "    `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "    `startDate` INTEGER, " +
                        "    `endDate` INTEGER, " +
                        "    `duration` INTEGER, " +
                        "    `workDayId` INTEGER NOT NULL" +
                        ")");

                database.execSQL("INSERT INTO come_event_tmp(startDate, endDate, duration, workDayId) " +
                        "SELECT startEvent.date AS startDate," +
                        "    MIN(endEvent.date) AS endDate," +
                        "    (MIN(endEvent.date) - startEvent.date) AS duration," +
                        "    startEvent.workDayId AS workDayId " +
                        "FROM (" +
                        "    SELECT date," +
                        "        workDayId" +
                        "    FROM come_event " +
                        "    WHERE type = 'COME_IN'" +
                        ") startEvent" +
                        "LEFT JOIN (" +
                        "    SELECT date," +
                        "        workDayId" +
                        "    FROM come_event " +
                        "    WHERE type = 'COME_OUT'" +
                        ") endEvent ON startEvent.workDayId = endEvent.workDayId" +
                        "    AND startEvent.date <= endEvent.date " +
                        "GROUP BY startDate");

                database.execSQL("DROP TABLE come_event");
                database.execSQL("ALTER TABLE come_event_tmp RENAME TO come_event");
            }
        });

        return migrations.toArray(new Migration[0]);
    }

    public abstract ComeEventDao comeEventDao();

    public abstract WorkDayDao workDayDao();
}
