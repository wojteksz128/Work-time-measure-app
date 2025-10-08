package net.wojteksz128.worktimemeasureapp.util.datetime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.apache.commons.net.ntp.NTPUDPClient
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class DateTimeProvider @Inject constructor(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    @ApplicationContext private val context: Context,
) : ClassTagAware {

    val currentTime: ZonedDateTime
        get() = getCorrectedTime()

    val currentTimeZone: ZoneId
        get() = ZoneId.systemDefault()

    private fun getCorrectedTime(): ZonedDateTime {
        val lastNtpTime = sharedPreferences.getLong("last_ntp_time", 0L)
        val lastSystemTime = sharedPreferences.getLong("last_system_time", 0L)
        val lastElapsedTime = sharedPreferences.getLong("last_elapsed_time", 0L)

        if (lastNtpTime == 0L || lastSystemTime == 0L || lastElapsedTime == 0L) {
            return getNtpTime() ?: ZonedDateTime.now()
        }

        val currentElapsedTime = SystemClock.elapsedRealtime()
        val elapsedTimeDiff = currentElapsedTime - lastElapsedTime
        val correctedNtpTime = lastNtpTime + elapsedTimeDiff

        return Instant.ofEpochMilli(correctedNtpTime).atZone(ZoneId.systemDefault())
    }

    val currentCalendar: Calendar
        get() = currentCalendarWithoutCorrection.apply { time = currentTime.toDate() }

    val currentCalendarWithoutCorrection: Calendar
        get() = Calendar.getInstance()

    val weekEndDay: LocalDate
        get() {
            val c = Calendar.getInstance()
            c.time =
                Date(weekBeginDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            c.add(Calendar.WEEK_OF_YEAR, 1)
            c.add(Calendar.MILLISECOND, -1)
            return Instant.ofEpochMilli(c.time.time).atZone(ZoneId.systemDefault()).toLocalDate()
        }

    val weekBeginDay: LocalDate
        get() {
            val firstWeekDay = Settings.WorkTime.Week.FirstWeekDay.valueNullable
            val c = Calendar.getInstance()
            val currentTime = currentTime
            c.time = currentTime.toDate()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.clear(Calendar.MINUTE)
            c.clear(Calendar.SECOND)
            c.clear(Calendar.MILLISECOND)
            c.set(Calendar.DAY_OF_WEEK, firstWeekDay ?: Calendar.MONDAY)
            if (c.time > currentTime.toDate())
                c.add(Calendar.WEEK_OF_YEAR, -1)

            return Instant.ofEpochMilli(c.time.time).atZone(ZoneId.systemDefault()).toLocalDate()
        }

    private val sharedPreferences = context.getSharedPreferences("time_prefs", MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun updateOffset() {
        val timeSyncEnabled = Settings.Sync.TimeSync.Enabled.value
        if (timeSyncEnabled) {
            coroutineScope.launch {
                val ntpTime = getNtpTime()
                ntpTime?.let {
                    val ntpTimeMillis = it.toInstant().toEpochMilli()
                    val systemTimeMillis = System.currentTimeMillis()
                    val elapsedTime = SystemClock.elapsedRealtime()

                    sharedPreferences.edit()
                        .putLong("last_ntp_time", ntpTimeMillis)
                        .putLong("last_system_time", systemTimeMillis)
                        .putLong("last_elapsed_time", elapsedTime)
                        .apply()
                }
            }
        }
    }

    private fun getNtpTime(): ZonedDateTime? {
        if (!Settings.Sync.TimeSync.Enabled.value)
            return null

        val client = NTPUDPClient()
        @Suppress("DEPRECATION")
        client.defaultTimeout = Duration.ofSeconds(5).toMillis().toInt()
        try {
            client.open()
            val address = Settings.Sync.TimeSync.ServerAddress.value
            val info = client.getTime(address)
            info.computeDetails()
            val ntpTime = info.returnTime
            return Instant.ofEpochMilli(ntpTime).atZone(ZoneId.systemDefault())
        } catch (e: Exception) {
            Log.e(classTag, "Failed to get the actual time.", e)
            return null
        } finally {
            client.close()
        }
    }
}

class RebootReceiver @Inject constructor(
    private val dateTimeProvider: DateTimeProvider,
) : BroadcastReceiver(), ClassTagAware {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            dateTimeProvider.updateOffset()
            Log.d(classTag, "Reboot detected. Attempting to sync NTP time.")
        }
    }
}