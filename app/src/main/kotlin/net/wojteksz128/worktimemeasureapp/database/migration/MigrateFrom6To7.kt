package net.wojteksz128.worktimemeasureapp.database.migration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import android.util.Log

class MigrateFrom6To7 : Migration(6, 7) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.d(MigrateFrom6To7::class.java.simpleName, "Begin migrate data from 6 to 7 db version")
        database.execSQL("PRAGMA foreign_keys=off")

        // work_day
        Log.d(MigrateFrom6To7::class.java.simpleName, "Migrate work_day table")
        database.execSQL("ALTER TABLE work_day RENAME TO _work_day_old")

        database.execSQL("CREATE TABLE IF NOT EXISTS `work_day` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `date` INTEGER NOT NULL, `beginSlot` INTEGER NOT NULL, `endSlot` INTEGER NOT NULL)")
        database.execSQL("INSERT INTO `work_day`(`id`, `date`, `beginSlot`, `endSlot`) SELECT `id`, `date`, `beginSlot`, `endSlot` FROM _work_day_old")

        // cleanup
        Log.d(MigrateFrom6To7::class.java.simpleName, "Cleanup temp tables")
        database.execSQL("DROP TABLE _work_day_old")

        database.execSQL("PRAGMA foreign_keys=on")
        Log.d(MigrateFrom6To7::class.java.simpleName, "End migrate data from 6 to 7 db version")
    }

}
