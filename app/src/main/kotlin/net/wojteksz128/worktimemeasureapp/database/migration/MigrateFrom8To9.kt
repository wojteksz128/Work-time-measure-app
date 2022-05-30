package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class MigrateFrom8To9 : Migration(8, 9), ClassTagAware {

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Begin migrate data from 8 to 9 db version")
        database.execSQL("PRAGMA foreign_keys=off")

        Log.d(classTag, "migrate: Migrate day_off table")
        database.execSQL("ALTER TABLE `day_off` RENAME TO `_day_off_old`")

        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `day_off` (
                |`id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                |`uuid` TEXT, 
                |`type` TEXT NOT NULL, 
                |`name` TEXT NOT NULL, 
                |`startDay` INTEGER NOT NULL, 
                |`startMonth` TEXT NOT NULL, 
                |`startYear` INTEGER, 
                |`finishDay` INTEGER NOT NULL, 
                |`finishMonth` TEXT NOT NULL, 
                |`finishYear` INTEGER, 
                |`source` TEXT NOT NULL
                |)""".trimMargin()
        )
        database.execSQL(
            """INSERT INTO `day_off` (`id`, 
                |`uuid`, 
                |`type`, 
                |`name`, 
                |`startDay`, 
                |`startMonth`, 
                |`startYear`, 
                |`finishDay`, 
                |`finishMonth`, 
                |`finishYear`, 
                |`source`) 
                |SELECT `id`, 
                |null, 
                |`type`, 
                |'', 
                |`startDay`, 
                |`startMonth`, 
                |`startYear`, 
                |`finishDay`, 
                |CASE
                |    WHEN `finishMonth` == 0 THEN 'JANUARY' 
                |    WHEN `finishMonth` == 1 THEN 'FEBRUARY' 
                |    WHEN `finishMonth` == 2 THEN 'MARCH' 
                |    WHEN `finishMonth` == 3 THEN 'APRIL' 
                |    WHEN `finishMonth` == 4 THEN 'MAY' 
                |    WHEN `finishMonth` == 5 THEN 'JUNE' 
                |    WHEN `finishMonth` == 6 THEN 'JULY' 
                |    WHEN `finishMonth` == 7 THEN 'AUGUST' 
                |    WHEN `finishMonth` == 8 THEN 'SEPTEMBER' 
                |    WHEN `finishMonth` == 9 THEN 'OCTOBER' 
                |    WHEN `finishMonth` == 10 THEN 'NOVEMBER' 
                |    WHEN `finishMonth` == 11 THEN 'DECEMBER' 
                |    ELSE 'UNRECOGENIZED' END, 
                |`finishYear`, 
                |`source` 
                |FROM `_day_off_old`
            """.trimMargin()
        )

        Log.d(classTag, "migrate: Cleanup temp tables")
        database.execSQL("DROP TABLE `_day_off_old`")

        database.execSQL("PRAGMA foreign_keys=on")
        Log.d(classTag, "migrate: End migrate data from 8 to 9 db version")
    }
}
