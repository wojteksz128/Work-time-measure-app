package net.wojteksz128.worktimemeasureapp.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDto
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.converter.ComeEventTypeConverter
import net.wojteksz128.worktimemeasureapp.database.converter.DateConverter
import net.wojteksz128.worktimemeasureapp.database.migration.*
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDto
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware

@Database(entities = [ComeEventDto::class, WorkDayDto::class], version = 7)
@TypeConverters(DateConverter::class, ComeEventTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comeEventDao(): ComeEventDao

    abstract fun workDayDao(): WorkDayDao

    companion object : ClassTagAware {

        private val LOCK = Any()
        const val DATABASE_FILENAME = "work-time-measure.db"
        private var sInstance: AppDatabase? = null

        // TODO: 30.09.2021 use hilt
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Log.d(classTag, "getInstance: Creating new database instance")
                    val filePath = context.getExternalFilesDir(null)!!.absolutePath + "/" + DATABASE_FILENAME
                    Log.d(classTag, "getInstance: Database path: $filePath")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, filePath)
                            .addMigrations(*databaseMigrations)
                            .build()
                }
            }
            Log.d(classTag, "getInstance: Getting the database instance")
            return sInstance!!
        }

        private val databaseMigrations: Array<Migration>
            get() = arrayOf(MigrateFrom1To2(), MigrateFrom2To3(), MigrateFrom3To4(), MigrateFrom4To5(), MigrateFrom5To6(), MigrateFrom6To7())
    }
}
