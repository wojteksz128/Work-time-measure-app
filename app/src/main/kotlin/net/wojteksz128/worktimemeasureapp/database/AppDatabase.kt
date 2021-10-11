package net.wojteksz128.worktimemeasureapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.converter.DateConverter
import net.wojteksz128.worktimemeasureapp.database.migration.*
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

@Database(entities = [ComeEventDto::class, WorkDayDto::class], version = 7)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comeEventDao(): ComeEventDao

    abstract fun workDayDao(): WorkDayDao

    companion object : ClassTagAware {
        const val DATABASE_FILENAME = "work-time-measure.db"

        internal val databaseMigrations: Array<Migration>
            get() = arrayOf(
                MigrateFrom1To2(),
                MigrateFrom2To3(),
                MigrateFrom3To4(),
                MigrateFrom4To5(),
                MigrateFrom5To6(),
                MigrateFrom6To7()
            )
    }
}
