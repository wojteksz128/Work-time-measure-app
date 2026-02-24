package net.wojteksz128.worktimemeasureapp.backup.migration

import android.util.Log
import com.google.gson.JsonObject
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import javax.inject.Inject

/**
 * Applies a chain of [BackupMigration]s to bring a backup [JsonObject]
 * from any older version up to the current one.
 *
 * Migrations are applied in ascending order of [BackupMigration.fromVersion].
 * If no migration exists for a given step, an [IllegalStateException] is thrown.
 */
class BackupMigrationRunner @Inject constructor(
    private val migrations: Set<@JvmSuppressWildcards BackupMigration>,
) : ClassTagAware {

    /**
     * Migrates [json] from [fromVersion] to [toVersion] by applying all
     * necessary [BackupMigration]s in order.
     *
     * @throws IllegalStateException if no migration exists for a required step.
     */
    fun migrate(json: JsonObject, fromVersion: Int, toVersion: Int) {
        if (fromVersion == toVersion) return

        Log.d(classTag, "Migrating backup from version $fromVersion to $toVersion")

        val migrationMap = migrations.associateBy { it.fromVersion }
        var currentVersion = fromVersion

        while (currentVersion < toVersion) {
            val migration = migrationMap[currentVersion]
                ?: throw IllegalStateException(
                    "No backup migration found from version $currentVersion. " +
                            "Available migrations: ${migrationMap.keys.sorted()}"
                )

            Log.d(classTag, "Applying migration: ${migration.fromVersion} → ${migration.toVersion}")
            migration.migrate(json)
            currentVersion = migration.toVersion
        }

        Log.d(classTag, "Backup migration completed. Final version: $currentVersion")
    }
}

