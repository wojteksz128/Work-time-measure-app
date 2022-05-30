package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class MigrateFrom7To8 : Migration(7, 8), ClassTagAware {
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Begin migrate data from 7 to 8 db version")

        // day_off
        Log.d(classTag, "migrate: Create day_off table")
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `day_off` (
                |`id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                |`type` TEXT NOT NULL, 
                |`startDay` INTEGER NOT NULL, 
                |`startMonth` TEXT NOT NULL, 
                |`startYear` INTEGER, 
                |`finishDay` INTEGER NOT NULL, 
                |`finishMonth` INTEGER NOT NULL, 
                |`finishYear` INTEGER, 
                |`source` TEXT NOT NULL
                |)""".trimMargin()
        )

        Log.d(classTag, "migrate: End migrate data from 7 to 8 db version")
    }

}
