package net.wojteksz128.worktimemeasureapp.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import java.util.UUID

class MigrateFrom12To13 : Migration(12, 13), ClassTagAware {

    override fun migrate(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") database: SupportSQLiteDatabase) {
        Log.d(classTag, "migrate: Begin migrate data from 12 to 13 db version")

        Log.d(classTag, "migrate: Add column 'changeGroupId' to 'entity_history' table")
        database.execSQL("ALTER TABLE `entity_history` ADD COLUMN `changeGroupId` TEXT NOT NULL DEFAULT ''")

        Log.d(classTag, "migrate: Get timestamps from 'entity_history' table")
        val cursor =
            database.query("SELECT DISTINCT `timestamp` FROM `entity_history` WHERE `changeGroupId` = ''")
        val timestamps = mutableListOf<String>()

        Log.d(classTag, "migrate: Query about timestamps returns ${cursor.count} rows")

        if (cursor.moveToFirst()) {
            do {
                val timestampIndex = cursor.getColumnIndex("timestamp")
                if (timestampIndex != -1) {
                    val timestamp = cursor.getString(timestampIndex)
                    Log.d(classTag, "migrate: Timestamp: $timestamp")
                    timestamps.add(timestamp)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        Log.d(classTag, "migrate: Found ${timestamps.size} timestamps. Start updating them")
        for (timestamp in timestamps) {
            val uuid = UUID.randomUUID().toString()
            Log.d(classTag, "migrate: Update timestamp: $timestamp, with uuid: $uuid")
            database.execSQL("UPDATE `entity_history` SET `changeGroupId` = '$uuid' WHERE `timestamp` = '$timestamp'")
        }

        Log.d(classTag, "migrate: End migrate data from 12 to 13 db version")
    }
}