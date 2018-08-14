package net.wojteksz128.worktimemeasureapp.database.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class MigrateFrom4To5 extends Migration {


    public MigrateFrom4To5() {
        super(4, 5);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS work_day_tmp " +
                "( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                ", `date` INTEGER" +
                ", `beginSlot` INTEGER" +
                ", `endSlot` INTEGER" +
                ", `percentDeclaredTime` REAL NOT NULL" +
                ")");

        database.execSQL("INSERT INTO work_day_tmp " +
                "SELECT id, date, beginSlot, endSlot, percentDeclaredTime " +
                "FROM work_day");

        database.execSQL("DROP TABLE work_day");
        database.execSQL("ALTER TABLE work_day_tmp RENAME TO work_day");
    }
}
