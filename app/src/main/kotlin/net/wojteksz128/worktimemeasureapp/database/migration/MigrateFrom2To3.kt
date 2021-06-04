package net.wojteksz128.worktimemeasureapp.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class MigrateFrom2To3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DELETE FROM work_day")
        database.execSQL("DELETE FROM come_event")

        database.execSQL("ALTER TABLE work_day ADD COLUMN beginSlot INTEGER")
        database.execSQL("ALTER TABLE work_day ADD COLUMN endSlot INTEGER")
    }
}
