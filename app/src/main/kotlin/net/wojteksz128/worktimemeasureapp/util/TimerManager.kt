package net.wojteksz128.worktimemeasureapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import net.wojteksz128.worktimemeasureapp.notification.TimerExpiredReceiver
import net.wojteksz128.worktimemeasureapp.settings.Settings
import java.util.Calendar

class TimerManager(
    private val context: Context,
    private val Settings: Settings
) {

    enum class AlarmState {
        NotSet, Set
    }

    fun setAlarm(wakeUpTime: Calendar): Long {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getTimerExpiredReceiverPendingIntent(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime.timeInMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpTime.timeInMillis, pendingIntent)
        }
        Settings.Internal.AlarmState.value = AlarmState.Set
        return wakeUpTime.timeInMillis
    }

    fun removeAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getTimerExpiredReceiverPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        Settings.Internal.AlarmState.value = AlarmState.NotSet
    }

    private fun getTimerExpiredReceiverPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, TimerExpiredReceiver::class.java)
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}