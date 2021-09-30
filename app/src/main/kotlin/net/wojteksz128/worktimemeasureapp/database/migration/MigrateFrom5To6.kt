package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject

class MigrateFrom5To6 @Inject constructor() : Migration(5, 6) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(MigrateFrom5To6::class.java.simpleName, "Begin migrate data from 5 to 6 db version")
        database.execSQL("PRAGMA foreign_keys=off")

        // work_day
        Log.d(MigrateFrom5To6::class.java.simpleName, "Migrate work_day table")
        database.execSQL("ALTER TABLE work_day RENAME TO _work_day_old")

        database.execSQL("CREATE TABLE IF NOT EXISTS `work_day` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `date` INTEGER, `beginSlot` INTEGER NOT NULL, `endSlot` INTEGER NOT NULL, `percentDeclaredTime` REAL NOT NULL)")
        database.execSQL("INSERT INTO `work_day`(`id`, `date`, `beginSlot`, `endSlot`, `percentDeclaredTime`) SELECT `id`, `date`, `beginSlot`, `endSlot`, `percentDeclaredTime` FROM _work_day_old")

        // come_event
        Log.d(MigrateFrom5To6::class.java.simpleName, "Migrate come_event table")
        database.execSQL("ALTER TABLE come_event RENAME TO _come_event_old")

        database.execSQL("CREATE TABLE IF NOT EXISTS `come_event` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `startDate` INTEGER NOT NULL, `endDate` INTEGER, `duration` INTEGER, `workDayId` INTEGER NOT NULL, FOREIGN KEY(`workDayId`) REFERENCES `work_day`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX `index_come_event_workDayId` ON `come_event` (`workDayId`)")
        database.execSQL("INSERT INTO `come_event`(`id`, `startDate`, `endDate`, `duration`, `workDayId`) SELECT `id`, `startDate`, `endDate`, `duration`, `workDayId` FROM _come_event_old")

        // cleanup
        Log.d(MigrateFrom5To6::class.java.simpleName, "Cleanup temp tables")
        database.execSQL("DROP TABLE _work_day_old")
        database.execSQL("DROP TABLE _come_event_old")

        database.execSQL("PRAGMA foreign_keys=on")
        Log.d(MigrateFrom5To6::class.java.simpleName, "End migrate data from 5 to 6 db version")
    }

}
