package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class MigrateFrom11To12 : Migration(11, 12), ClassTagAware {

    override fun migrate(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Begin migrate data from 11 to 12 db version")
        Log.d(classTag, "migrate: Create table 'entity_history'")
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `entity_history` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                    `entityType` TEXT NOT NULL,
                    `entityId` INTEGER NOT NULL,
                    `actionType` TEXT NOT NULL,
                    `fieldName` TEXT NOT NULL,
                    `oldValue` TEXT,
                    `newValue` TEXT,
                    `timestamp` TEXT NOT NULL
                )
            """.trimIndent()
        )
        Log.d(classTag, "migrate: End migrate data from 11 to 12 db version")
    }
}