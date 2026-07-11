package net.wojteksz128.worktimemeasureapp.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Duration

@Parcelize
data class WorkTimeRequirements(
    val standardRequiredToday: Duration,
    val monthlyBalance: Duration,
) : Parcelable {

    @IgnoredOnParcel
    val balancedRequiredToday: Duration = standardRequiredToday - monthlyBalance
}