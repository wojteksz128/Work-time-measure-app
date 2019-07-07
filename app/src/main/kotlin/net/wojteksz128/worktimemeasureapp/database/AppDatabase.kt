package net.wojteksz128.worktimemeasureapp.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.util.Log
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEvent
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.converter.ComeEventTypeConverter
import net.wojteksz128.worktimemeasureapp.database.converter.DateConverter
import net.wojteksz128.worktimemeasureapp.database.migration.*
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao

@Database(entities = [ComeEvent::class, WorkDay::class], version = 7)
@TypeConverters(DateConverter::class, ComeEventTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comeEventDao(): ComeEventDao

    abstract fun workDayDao(): WorkDayDao

    companion object {

        private val LOG_TAG = AppDatabase::class.java.simpleName
        private val LOCK = Any()
        private const val DATABASE_FILENAME = "work-time-measure.db"
        private val TAG = AppDatabase::class.java.simpleName
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Log.d(LOG_TAG, "getInstance: Creating new database instance")
                    val filePath = context.getExternalFilesDir(null)!!.absolutePath + "/" + DATABASE_FILENAME
                    Log.d(TAG, "getInstance: Database path: $filePath")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, filePath)
                            .addMigrations(*databaseMigrations)
                            .build()
                }
            }
            Log.d(LOG_TAG, "getInstance: Getting the database instance")
            return sInstance!!
        }

        private val databaseMigrations: Array<Migration>
            get() = arrayOf(MigrateFrom1To2(), MigrateFrom2To3(), MigrateFrom3To4(), MigrateFrom4To5(), MigrateFrom5To6(), MigrateFrom6To7())
    }
}
