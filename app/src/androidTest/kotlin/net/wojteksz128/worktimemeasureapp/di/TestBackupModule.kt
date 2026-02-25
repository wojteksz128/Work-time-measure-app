package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dagger.multibindings.ElementsIntoSet
import net.wojteksz128.worktimemeasureapp.backup.BackupService
import net.wojteksz128.worktimemeasureapp.backup.migration.BackupMigration
import net.wojteksz128.worktimemeasureapp.backup.migration.BackupMigrationRunner
import org.mockito.kotlin.mock
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [BackupMigrationModule::class]
)
object TestBackupModule {

    @Singleton
    @Provides
    @ElementsIntoSet
    fun provideBackupMigrations(): Set<BackupMigration> = emptySet()

    @Singleton
    @Provides
    fun provideBackupMigrationRunner(
        migrations: Set<@JvmSuppressWildcards BackupMigration>,
    ): BackupMigrationRunner = BackupMigrationRunner(migrations)

    @Singleton
    @Provides
    fun provideBackupService(): BackupService = mock()
}


