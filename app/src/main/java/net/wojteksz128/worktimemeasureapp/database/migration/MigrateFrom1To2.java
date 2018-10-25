package net.wojteksz128.worktimemeasureapp.database.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class MigrateFrom1To2 extends Migration {


    public MigrateFrom1To2() {
        super(1, 2);
    }

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
}
