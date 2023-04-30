package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class MigrateFrom9To10 : Migration(9, 10), ClassTagAware {

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Begin migrate data from 9 to 10 db version")
        database.execSQL("PRAGMA foreign_keys=off")

        Log.d(classTag, "migrate: Migrate day_off table")
        database.execSQL("ALTER TABLE `day_off` RENAME TO `_day_off_old`")

        database.execSQL(
            """
                |CREATE TABLE IF NOT EXISTS `day_off` (
                |    `id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                |    `uuid` TEXT, 
                |    `type` TEXT NOT NULL, 
                |    `name` TEXT NOT NULL, 
                |    `startDate` TEXT NOT NULL, 
                |    `finishDate` TEXT NOT NULL, 
                |    `source` TEXT NOT NULL
                |)""".trimMargin()
        )
        database.execSQL(
            """
                |INSERT INTO `day_off` (
                |    `id`, 
                |    `uuid`, 
                |    `type`, 
                |    `name`, 
                |    `startDate`, 
                |    `finishDate`, 
                |    `source`) 
                |SELECT `id`, 
                |    `uuid`, 
                |    `type`, 
                |    `name`, 
                |    `startYear` || '-' || CASE
                |        WHEN `startMonth` == 'JANUARY' THEN '01'
                |        WHEN `startMonth` == 'FEBRUARY' THEN '02'
                |        WHEN `startMonth` == 'MARCH' THEN '03'
                |        WHEN `startMonth` == 'APRIL' THEN '04'
                |        WHEN `startMonth` == 'MAY' THEN '05'
                |        WHEN `startMonth` == 'JUNE' THEN '06'
                |        WHEN `startMonth` == 'JULY' THEN '07'
                |        WHEN `startMonth` == 'AUGUST' THEN '08'
                |        WHEN `startMonth` == 'SEPTEMBER' THEN '09'
                |        WHEN `startMonth` == 'OCTOBER' THEN '10'
                |        WHEN `startMonth` == 'NOVEMBER' THEN '11'
                |        WHEN `startMonth` == 'DECEMBER' THEN '12'
                |    ELSE 'UNRECOGNIZED' END || '-' || CASE WHEN `startDay` < 10 THEN '0' ELSE '' END || `startDay`, 
                |    `finishYear` || '-' || CASE
                |        WHEN `finishMonth` == 'JANUARY' THEN '01'
                |        WHEN `finishMonth` == 'FEBRUARY' THEN '02'
                |        WHEN `finishMonth` == 'MARCH' THEN '03'
                |        WHEN `finishMonth` == 'APRIL' THEN '04'
                |        WHEN `finishMonth` == 'MAY' THEN '05'
                |        WHEN `finishMonth` == 'JUNE' THEN '06'
                |        WHEN `finishMonth` == 'JULY' THEN '07'
                |        WHEN `finishMonth` == 'AUGUST' THEN '08'
                |        WHEN `finishMonth` == 'SEPTEMBER' THEN '09'
                |        WHEN `finishMonth` == 'OCTOBER' THEN '10'
                |        WHEN `finishMonth` == 'NOVEMBER' THEN '11'
                |        WHEN `finishMonth` == 'DECEMBER' THEN '12'
                |    ELSE 'UNRECOGNIZED' END || '-' || CASE WHEN `finishDay` < 10 THEN '0' ELSE '' END || `finishDay`,
                |    `source` 
                |FROM `_day_off_old`
            """.trimMargin()
        )

        Log.d(classTag, "migrate: Cleanup temp tables")
        database.execSQL("DROP TABLE `_day_off_old`")

        database.execSQL("PRAGMA foreign_keys=on")
        Log.d(classTag, "migrate: End migrate data from 9 to 10 db version")
    }
}