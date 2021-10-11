package net.wojteksz128.worktimemeasureapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils

@HiltAndroidApp
class WorkTimeMeasureApp : Application() {

    override fun onCreate() {
        super.onCreate()

        NotificationUtils.initNotifications(this)
    }
}