package net.wojteksz128.worktimemeasureapp.util.datetime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.apache.commons.net.ntp.NTPUDPClient
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

open class DateTimeProvider @Inject constructor(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    @ApplicationContext private val context: Context,
) : ClassTagAware {

    open val currentTime: ZonedDateTime
        get() = getCorrectedTime()

    open val currentDate: LocalDate
        get() = currentTime.toLocalDate()

    open val currentTimeZone: ZoneId
        get() = ZoneId.systemDefault()

    private fun getCorrectedTime(): ZonedDateTime {
        val lastNtpTime = sharedPreferences.getLong("last_ntp_time", 0L)
        val lastSystemTime = sharedPreferences.getLong("last_system_time", 0L)
        val lastElapsedTime = sharedPreferences.getLong("last_elapsed_time", 0L)

        if (lastNtpTime == 0L || lastSystemTime == 0L || lastElapsedTime == 0L) {
            runBlocking {
                return@runBlocking getNtpTime() ?: ZonedDateTime.now()
            }
        }

        val currentElapsedTime = SystemClock.elapsedRealtime()
        val elapsedTimeDiff = currentElapsedTime - lastElapsedTime
        val correctedNtpTime = lastNtpTime + elapsedTimeDiff

        return Instant.ofEpochMilli(correctedNtpTime).atZone(ZoneId.systemDefault())
    }

    open val weekEndDay: LocalDate
        get() {
            return weekBeginDay.plusWeeks(1).minusDays(1)
        }

    open val weekBeginDay: LocalDate
        get() {
            val firstWeekDay =
                Settings.WorkTime.Week.FirstWeekDay.valueNullable?.let { DayOfWeek.valueOf(it) }
                    ?: DayOfWeek.MONDAY
            val currentDate = currentTime.toLocalDate()
            val currentDayOfWeek = currentDate.dayOfWeek
            val previousFirstDayOfWeekDiff =
                if (currentDayOfWeek >= firstWeekDay) currentDayOfWeek.value - firstWeekDay.value
                else DayOfWeek.entries.size - firstWeekDay.value + currentDayOfWeek.value

            return currentDate.minusDays(previousFirstDayOfWeekDiff.toLong())
        }

    private val sharedPreferences = context.getSharedPreferences("time_prefs", MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    open fun updateOffset() {
        val timeSyncEnabled = Settings.Sync.TimeSync.Enabled.value
        if (timeSyncEnabled) {
            coroutineScope.launch {
                val ntpTime = getNtpTime()
                ntpTime?.let {
                    val ntpTimeMillis = it.toInstant().toEpochMilli()
                    val systemTimeMillis = System.currentTimeMillis()
                    val elapsedTime = SystemClock.elapsedRealtime()

                    sharedPreferences.edit {
                        putLong("last_ntp_time", ntpTimeMillis)
                            .putLong("last_system_time", systemTimeMillis)
                            .putLong("last_elapsed_time", elapsedTime)
                    }
                }
            }
        }
    }

    private suspend fun getNtpTime(): ZonedDateTime? {
        if (!Settings.Sync.TimeSync.Enabled.value)
            return null

        val client = NTPUDPClient()
        @Suppress("DEPRECATION")
        client.defaultTimeout = Duration.ofSeconds(5).toMillis().toInt()
        try {
            client.open()
            val address = Settings.Sync.TimeSync.ServerAddress.getValueAsync()
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
            Log.d(classTag, "Reboot detected. Attempting to sync NTP time.")
            dateTimeProvider.updateOffset()
        }
    }
}