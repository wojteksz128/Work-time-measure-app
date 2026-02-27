package net.wojteksz128.worktimemeasureapp.util.fixtures

import net.wojteksz128.worktimemeasureapp.util.datetime.WorkTimeBalance
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

/**
 * Builder for [WorkTimeBalance] test objects.
 *
 * Usage:
 * ```kotlin
 * // balanced 8-hour day, zero monthly balance
 * val balance = aWorkTimeBalance()
 *
 * // partially worked day
 * val balance = aWorkTimeBalance {
 *     todayWorkTime = Duration.ofHours(4)
 * }
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
fun aWorkTimeBalance(
    currentTime: ZonedDateTime,
    block: WorkTimeBalanceBuilder.() -> Unit = {},
): WorkTimeBalance = WorkTimeBalanceBuilder(currentTime).apply(block).build()

class WorkTimeBalanceBuilder(private val currentTime: ZonedDateTime) {

    var todayWorkTime: Duration = Duration.ZERO
    var standardRequiredToday: Duration = Duration.ofHours(8)
    var monthlyBalance: Duration = Duration.ZERO

    fun build(): WorkTimeBalance = WorkTimeBalance(
        currentTime = currentTime,
        todayWorkTime = todayWorkTime,
        standardRequiredToday = standardRequiredToday,
        monthlyBalance = monthlyBalance,
    )
}

