package net.wojteksz128.worktimemeasureapp.configuration

import org.joda.time.Duration

object ConfigurationProvider {
    val workTimeDuration: Duration
        //        get() = Duration.millis(8*60*60*1000 + 30*60*1000)
        get() = Duration.millis(10 * 1000)
}