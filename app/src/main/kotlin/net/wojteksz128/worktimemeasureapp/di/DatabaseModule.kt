package net.wojteksz128.worktimemeasureapp.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.database.AppDatabase
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventDao
import net.wojteksz128.worktimemeasureapp.database.migration.*
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule : ClassTagAware {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        Log.d(classTag, "getInstance: Creating new database instance")

        val filePath = context.getExternalFilesDir(null)!!.absolutePath + "/" + AppDatabase.DATABASE_FILENAME
        Log.d(classTag, "getInstance: Database path: $filePath")

        return Room.databaseBuilder(context.applicationContext,
            AppDatabase::class.java, filePath)
            .addMigrations(*AppDatabase.databaseMigrations)
            .build()
    }

    @Singleton
    @Provides
    fun provideWorkDayDao(database: AppDatabase): WorkDayDao {
        return database.workDayDao()
    }

    @Singleton
    @Provides
    fun provideComeEventDao(database: AppDatabase): ComeEventDao {
        return database.comeEventDao()
    }
}