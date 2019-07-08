package net.wojteksz128.worktimemeasureapp.database.migration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration


class MigrateFrom3To4 : Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS come_event_tmp (" +
                "    `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "    `startDate` INTEGER, " +
                "    `endDate` INTEGER, " +
                "    `duration` INTEGER, " +
                "    `workDayId` INTEGER NOT NULL" +
                ")")

        database.execSQL("INSERT INTO come_event_tmp(startDate, endDate, duration, workDayId) " +
                "SELECT startEvent.date AS startDate," +
                "    MIN(endEvent.date) AS endDate," +
                "    (MIN(endEvent.date) - startEvent.date) AS duration," +
                "    startEvent.workDayId AS workDayId " +
                "FROM (" +
                "    SELECT date," +
                "        workDayId" +
                "    FROM come_event " +
                "    WHERE type = 'COME_IN'" +
                ") startEvent" +
                "LEFT JOIN (" +
                "    SELECT date," +
                "        workDayId" +
                "    FROM come_event " +
                "    WHERE type = 'COME_OUT'" +
                ") endEvent ON startEvent.workDayId = endEvent.workDayId" +
                "    AND startEvent.date <= endEvent.date " +
                "GROUP BY startDate")

        database.execSQL("DROP TABLE come_event")
        database.execSQL("ALTER TABLE come_event_tmp RENAME TO come_event")
    }
}
