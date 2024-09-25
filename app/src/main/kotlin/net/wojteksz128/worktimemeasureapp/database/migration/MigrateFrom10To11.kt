package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

class MigrateFrom10To11 : Migration(10, 11), ClassTagAware {

    private val formattedZoneId = ZoneId.systemDefault().let {
        "${it.rules.getOffset(Instant.now()).id}[${it.id}]"
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Begin migrate data from 10 to 11 db version")
        database.execSQL("PRAGMA foreign_keys=off")

        convertWorkDayDatesToISO(database)
        convertComeEventDatesToISO(database)
        cleanupOldTables(database)

        database.execSQL("PRAGMA foreign_keys=on")
        Log.d(classTag, "migrate: End migrate data from 10 to 11 db version")
    }

    private fun convertWorkDayDatesToISO(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Migrate work_day table")
        database.execSQL("ALTER TABLE `work_day` RENAME TO `_work_day_old`")
        database.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `work_day` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                        `date` TEXT NOT NULL, 
                        `beginSlot` TEXT NOT NULL, 
                        `endSlot` TEXT NOT NULL
                    )
                """.trimIndent()
        )
        database.execSQL(
            """
                    INSERT INTO `work_day` (
                        `id`, 
                        `date`, 
                        `beginSlot`, 
                        `endSlot`
                    ) 
                    SELECT 
                        `id`, 
                        strftime('%Y-%m-%d', `date`/1000.0, 'unixepoch'),
                        strftime('%Y-%m-%dT%H:%M:%f${formattedZoneId}', `beginSlot`/1000.0, 'unixepoch'), 
                        strftime('%Y-%m-%dT%H:%M:%f${formattedZoneId}', `endSlot`/1000.0, 'unixepoch') 
                    FROM `_work_day_old`
                """.trimIndent()
        )
    }

    private fun convertComeEventDatesToISO(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Migrate come_event table")
        database.execSQL("ALTER TABLE `come_event` RENAME TO `_come_event_old`")
        database.execSQL("DROP INDEX `index_come_event_workDayId`")
        database.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS `come_event` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                        `startDate` TEXT NOT NULL, 
                        `endDate` TEXT,
                        `duration` INTEGER,
                        `workDayId` INTEGER NOT NULL, 
                        FOREIGN KEY(`workDayId`) REFERENCES `work_day`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )""".trimIndent()
        )
        database.execSQL("CREATE INDEX `index_come_event_workDayId` ON `come_event` (`workDayId`)")
        database.execSQL(
            """
                    INSERT INTO `come_event` (
                        `id`,
                        `startDate`,
                        `endDate`,
                        `duration`,
                        `workDayId`
                    ) 
                    SELECT 
                        `id`, 
                        strftime('%Y-%m-%dT%H:%M:%f', `startDate`/1000.0, 'unixepoch'), 
                        strftime('%Y-%m-%dT%H:%M:%f', `endDate`/1000.0, 'unixepoch'), 
                        `duration`, 
                        `workDayId` 
                    FROM `_come_event_old`
                """.trimIndent()
        )
    }

    private fun cleanupOldTables(database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Cleanup old tables")
        database.execSQL("DROP TABLE `_work_day_old`")
        database.execSQL("DROP TABLE `_come_event_old`")
    }
}