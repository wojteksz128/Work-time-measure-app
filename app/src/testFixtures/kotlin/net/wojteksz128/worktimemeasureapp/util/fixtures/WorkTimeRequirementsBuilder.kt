package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.model.WorkTimeRequirements
import org.threeten.bp.Duration

/**
 * Builder for [WorkTimeRequirements] test objects.
 *
 * Usage:
 * ```kotlin
 * // balanced 8-hour day, zero monthly balance
 * val balance = aWorkTimeBalance()
 *
 * // day with positive monthly balance (overtime from previous days)
 * val balance = aWorkTimeBalance {
 *     monthlyBalance = Duration.ofHours(2)
 * }
 *
 * // day off (no work required)
 * val balance = aWorkTimeBalance {
 *     standardRequiredToday = Duration.ZERO
 * }
 * ```
 */
fun aWorkTimeRequirements(
    block: WorkTimeRequirementsBuilder.() -> Unit = {},
): WorkTimeRequirements = WorkTimeRequirementsBuilder().apply(block).build()

class WorkTimeRequirementsBuilder {

    var standardRequiredToday: Duration = Duration.ofHours(8)
    var monthlyBalance: Duration = Duration.ZERO

    fun build(): WorkTimeRequirements = WorkTimeRequirements(
        standardRequiredToday = standardRequiredToday,
        monthlyBalance = monthlyBalance,
    )
}

