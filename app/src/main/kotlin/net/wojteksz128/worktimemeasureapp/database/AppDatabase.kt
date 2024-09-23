package net.wojteksz128.worktimemeasureapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.converter.DateConverters
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDao
import net.wojteksz128.worktimemeasureapp.database.dayOff.DayOffDto
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom10To11
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom1To2
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom2To3
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom3To4
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom4To5
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom5To6
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom6To7
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom7To8
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom8To9
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom9To10
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

@Database(
    entities = [
        ComeEventDto::class,
        WorkDayDto::class,
        DayOffDto::class
    ],
    version = 11
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comeEventDao(): ComeEventDao

    abstract fun workDayDao(): WorkDayDao

    abstract fun dayOffDao(): DayOffDao

    companion object : ClassTagAware {
        const val DATABASE_FILENAME = "work-time-measure.db"

        internal val databaseMigrations: Array<Migration>
            get() = arrayOf(
                MigrateFrom1To2(),
                MigrateFrom2To3(),
                MigrateFrom3To4(),
                MigrateFrom4To5(),
                MigrateFrom5To6(),
                MigrateFrom6To7(),
                MigrateFrom7To8(),
                MigrateFrom8To9(),
                MigrateFrom9To10(),
                MigrateFrom10To11()
            )
    }
}
