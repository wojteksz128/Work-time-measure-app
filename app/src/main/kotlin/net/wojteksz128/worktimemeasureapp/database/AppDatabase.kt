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
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom1To2
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom2To3
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom3To4
import net.wojteksz128.worktimemeasureapp.database.migration.MigrateFrom4To5
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDay
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import java.util.*

@Database(entities = [ComeEvent::class, WorkDay::class], version = 5)
@TypeConverters(DatabaseConverters.DateConverter::class, DatabaseConverters.ComeEventTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comeEventDao(): ComeEventDao

    abstract fun workDayDao(): WorkDayDao

    companion object {

        private val LOG_TAG = AppDatabase::class.java.simpleName
        private val LOCK = Any()
        private val DATABASE_FILENAME = "work-time-measure.db"
        private val TAG = AppDatabase::class.java.simpleName
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Log.d(LOG_TAG, "getInstance: Creating new database instance")
                    val filePath = context.getExternalFilesDir(null)!!.absolutePath + "/" + AppDatabase.DATABASE_FILENAME
                    Log.d(TAG, "getInstance: Database path: $filePath")
                    sInstance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, filePath)
                            .addMigrations(*databaseMigrations)
                            .build()
                }
            }
            Log.d(LOG_TAG, "getInstance: Getting the database instance")
            return sInstance
        }

        private// Version 1    ->      2
        // Version 2    ->      3
        // Version 3    ->      4
        // Version 4    ->      5
        val databaseMigrations: Array<Migration>
            get() = Arrays.asList(
                    MigrateFrom1To2(),
                    MigrateFrom2To3(),
                    MigrateFrom3To4(),
                    MigrateFrom4To5()
            ).toTypedArray()
    }
}
