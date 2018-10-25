package net.wojteksz128.worktimemeasureapp.database.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class MigrateFrom2To3 extends Migration {


    public MigrateFrom2To3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("DELETE FROM work_day");
        database.execSQL("DELETE FROM come_event");

        database.execSQL("ALTER TABLE work_day ADD COLUMN beginSlot INTEGER");
        database.execSQL("ALTER TABLE work_day ADD COLUMN endSlot INTEGER");
    }
}
