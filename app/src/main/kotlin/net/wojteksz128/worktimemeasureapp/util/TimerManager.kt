package net.wojteksz128.worktimemeasureapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import net.wojteksz128.worktimemeasureapp.notification.TimerExpiredReceiver
import net.wojteksz128.worktimemeasureapp.settings.Settings
import org.threeten.bp.ZonedDateTime

class TimerManager(
    private val context: Context,
    @Suppress("PrivatePropertyName") private val Settings: Settings,
) {

    enum class AlarmState {
        NotSet, Set
    }

    fun setAlarm(wakeUpTime: ZonedDateTime): Long {
        val epochMilli = wakeUpTime.toInstant().toEpochMilli()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getTimerExpiredReceiverPendingIntent(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    epochMilli,
                    pendingIntent
                )
            } else {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    epochMilli,
                    1000,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, epochMilli, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, epochMilli, pendingIntent)
        }
        Settings.Internal.AlarmState.value = AlarmState.Set
        return epochMilli
    }

    fun removeAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getTimerExpiredReceiverPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        Settings.Internal.AlarmState.value = AlarmState.NotSet
    }

    private fun getTimerExpiredReceiverPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, TimerExpiredReceiver::class.java)
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)!!
    }
}