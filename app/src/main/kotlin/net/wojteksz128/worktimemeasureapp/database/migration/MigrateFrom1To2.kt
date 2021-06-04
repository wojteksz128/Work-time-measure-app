package net.wojteksz128.worktimemeasureapp.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class MigrateFrom1To2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE come_event")

        database.execSQL("CREATE TABLE IF NOT EXISTS `work_day` " +
                "( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                ", `date` INTEGER" +
                ", `worktime` INTEGER" +
                ", `percentDeclaredTime` REAL NOT NULL" +
                ")")

        database.execSQL("CREATE TABLE IF NOT EXISTS come_event " +
                "( `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                ", `date` INTEGER" +
                ", `type` TEXT" +
                ", `workDayId` INTEGER NOT NULL" +
                ")")
    }
}
