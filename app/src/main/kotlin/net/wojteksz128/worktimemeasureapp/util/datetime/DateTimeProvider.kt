package net.wojteksz128.worktimemeasureapp.util.datetime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import org.apache.commons.net.ntp.NTPUDPClient
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

private const val KEY_NTP_OFFSET = "ntp_offset"

open class DateTimeProvider @Inject constructor(
    @Suppress("PrivatePropertyName") private val Settings: Settings,
    @param:ApplicationContext private val context: Context,
) : ClassTagAware {

    open val currentTime: ZonedDateTime
        get() = getCorrectedTime()

    open val currentDate: LocalDate
        get() = currentTime.toLocalDate()

    open val currentTimeZone: ZoneId
        get() = ZoneId.systemDefault()

    /**
     * Flow emitting the current date. Reacts to system date change events:
     * - [Intent.ACTION_DATE_CHANGED] – date change in settings or at midnight,
     * - [Intent.ACTION_TIME_CHANGED] – manual time change, which may also shift the date.
     *
     * Thanks to [distinctUntilChanged], a new value is emitted only when the date actually changes
     * (e.g. a time change without crossing midnight will not trigger unnecessary refresh).
     */
    open val currentDateFlow: Flow<LocalDate>
        get() = callbackFlow {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Log.d(classTag, "Date/time broadcast received: ${intent?.action}")
                    updateOffset()
                    trySend(currentDate.apply { Log.d(classTag, "Current date: $this") })
                }
            }
            val filter = android.content.IntentFilter().apply {
                addAction(Intent.ACTION_DATE_CHANGED)
                addAction(Intent.ACTION_TIME_CHANGED)
            }
            context.registerReceiver(receiver, filter)
            awaitClose { context.unregisterReceiver(receiver) }
        }
            .onStart { emit(currentDate) }
            .map { currentDate }
            .distinctUntilChanged()

    private fun getCorrectedTime(): ZonedDateTime {
        val ntpOffset = sharedPreferences.getLong(KEY_NTP_OFFSET, Long.MIN_VALUE)
        return Instant.now().plusMillis(
            if (Settings.Sync.TimeSync.Enabled.value && ntpOffset != Long.MIN_VALUE) {
                ntpOffset
            } else {
                0
            }
        ).atZone(ZoneId.systemDefault())
    }

    private val sharedPreferences = context.getSharedPreferences("time_prefs", MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    open fun updateOffset() {
        val timeSyncEnabled = Settings.Sync.TimeSync.Enabled.value
        if (timeSyncEnabled) {
            coroutineScope.launch {
                getNtpOffset()?.let {
                    Log.d(classTag, "NTP offset updated: ${it}ms")
                    sharedPreferences.edit { putLong(KEY_NTP_OFFSET, it) }
                }
            }
        }
    }

    /**
     * Returns the offset in ms: (NTP time) − (system time).
     *
     * Uses [org.apache.commons.net.ntp.TimeInfo.offset], computed by
     * [org.apache.commons.net.ntp.TimeInfo.computeDetails] as:
     *   offset = ((receiveTime - originateTime) + (transmitTime - destinationTime)) / 2
     * in accordance with RFC 5905.
     */
    private suspend fun getNtpOffset(): Long? {
        if (!Settings.Sync.TimeSync.Enabled.value)
            return null

        val client = NTPUDPClient()
        @Suppress("DEPRECATION")
        client.defaultTimeout = Duration.ofSeconds(5).toMillis().toInt()
        return try {
            client.open()
            val address = Settings.Sync.TimeSync.ServerAddress.getValueAsync()
            val info = client.getTime(address)
            info.computeDetails()
            info.offset
        } catch (e: Exception) {
            Log.e(classTag, "Failed to get the NTP time.", e)
            null
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