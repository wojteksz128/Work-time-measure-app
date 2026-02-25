package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import net.wojteksz128.worktimemeasureapp.backup.migration.BackupMigration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupMigrationModule {

    /**
     * Provides the set of all registered [BackupMigration]s.
     *
     * When adding a new backup version, create a new [BackupMigration] implementation
     * and add it here with `@IntoSet`.
     *
     * Example for a future migration from version 1 to 2:
     * ```kotlin
     * @Singleton
     * @Provides
     * @IntoSet
     * fun provideBackupMigrationFrom1To2(): BackupMigration = BackupMigrationFrom1To2()
     * ```
     */
    @Singleton
    @Provides
    @ElementsIntoSet
    fun provideBackupMigrations(): Set<BackupMigration> = emptySet()
}

