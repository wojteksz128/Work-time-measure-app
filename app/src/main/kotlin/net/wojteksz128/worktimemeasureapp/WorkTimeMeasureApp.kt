package net.wojteksz128.worktimemeasureapp

import android.app.Activity
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltAndroidApp
class WorkTimeMeasureApp : Application() {

    @Inject
    lateinit var notificationUtils: NotificationUtils

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    override fun onCreate() {
        super.onCreate()

        initialSettingsPreparer.initSettings()
        notificationUtils.initNotifications()
    }

    fun closeApp(activity: Activity) {
        activity.finishAffinity()
        exitProcess(0)
    }
}