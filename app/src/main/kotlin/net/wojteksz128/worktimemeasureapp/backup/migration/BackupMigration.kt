package net.wojteksz128.worktimemeasureapp.backup.migration

import com.google.gson.JsonObject

/**
 * Represents a single migration step between two backup format versions.
 *
 * Migrations operate on the raw [JsonObject] before it is deserialized into [net.wojteksz128.worktimemeasureapp.backup.BackupData],
 * which allows transforming the structure freely (rename fields, add defaults, etc.).
 *
 * Naming convention for implementations: `BackupMigrationFrom_X_To_Y`
 * (e.g. BackupMigrationFrom1To2).
 *
 * ### How to add a new version:
 * 1. Increment [net.wojteksz128.worktimemeasureapp.backup.BackupService.Companion.CURRENT_BACKUP_VERSION] in [net.wojteksz128.worktimemeasureapp.backup.BackupData].
 * 2. Create a new class implementing [BackupMigration] with the appropriate
 *    [fromVersion]/[toVersion] values and migration logic in [migrate].
 * 3. Register it in [BackupMigrationRunner] (injected via Hilt in the DI module).
 * 4. Document the structural changes in the class KDoc.
 */
interface BackupMigration {

    /** Version this migration upgrades from. */
    val fromVersion: Int

    /** Version this migration upgrades to. */
    val toVersion: Int

    /**
     * Applies the migration to [json] in-place.
     *
     * The [json] object represents the full backup file content
     * (top-level fields: `workDays`, `comeEvents`, `daysOff`, `history`, `settings`, etc.).
     */
    fun migrate(json: JsonObject)
}

