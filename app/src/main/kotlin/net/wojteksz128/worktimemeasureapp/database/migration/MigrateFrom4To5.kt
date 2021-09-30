package net.wojteksz128.worktimemeasureapp.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject


class MigrateFrom4To5 @Inject constructor() : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS work_day_tmp " +
                "( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                ", `date` INTEGER" +
                ", `beginSlot` INTEGER" +
                ", `endSlot` INTEGER" +
                ", `percentDeclaredTime` REAL NOT NULL" +
                ")")

        database.execSQL("INSERT INTO work_day_tmp " +
                "SELECT id, date, beginSlot, endSlot, percentDeclaredTime " +
                "FROM work_day")

        database.execSQL("DROP TABLE work_day")
        database.execSQL("ALTER TABLE work_day_tmp RENAME TO work_day")
    }
}
