package net.wojteksz128.worktimemeasureapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import javax.inject.Inject

@HiltAndroidApp
class WorkTimeMeasureApp : Application() {

    @Inject
    lateinit var notificationUtils: NotificationUtils

    override fun onCreate() {
        super.onCreate()

        notificationUtils.initNotifications()
    }
}