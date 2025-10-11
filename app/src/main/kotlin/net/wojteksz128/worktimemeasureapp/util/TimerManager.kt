package net.wojteksz128.worktimemeasureapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class TimerManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : ClassTagAware {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    fun setExactTimer(wakeUpTime: ZonedDateTime, pendingIntent: PendingIntent) {
        val triggerAtMillis = wakeUpTime.toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w(
                classTag,
                "setExactTimer: App not have permission to schedule exact alarms. Setting timer instead."
            )
            setTimer(wakeUpTime, pendingIntent)
            return
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.i(
                    classTag,
                    "setExactTimer: Scheduling exact alarm using setExactAndAllowWhileIdle."
                )
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                Log.i(classTag, "setExactTimer: Scheduling exact alarm using setExact.")
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.w(
                classTag,
                "setExactTimer: Error during scheduling exact alarm. Setting timer instead.",
                e
            )
            setTimer(wakeUpTime, pendingIntent)
        }
    }

    fun setTimer(wakeUpTime: ZonedDateTime, pendingIntent: PendingIntent) {
        val triggerAtMillis = wakeUpTime.toInstant().toEpochMilli()
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    fun removeAlarm(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}