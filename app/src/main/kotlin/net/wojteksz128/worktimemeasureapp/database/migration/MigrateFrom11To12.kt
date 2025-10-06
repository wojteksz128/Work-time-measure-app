package net.wojteksz128.worktimemeasureapp.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

class MigrateFrom11To12 : Migration(11, 12), ClassTagAware {

    override fun migrate(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") database: SupportSQLiteDatabase) {
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
    }
}