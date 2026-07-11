package net.wojteksz128.worktimemeasureapp

import android.app.Activity
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import net.wojteksz128.worktimemeasureapp.notification.NotificationChannelCreator
import net.wojteksz128.worktimemeasureapp.settings.InitialSettingsPreparer
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltAndroidApp
class WorkTimeMeasureApp : Application() {

    @Inject
    lateinit var notificationChannelCreator: NotificationChannelCreator

    @Inject
    lateinit var initialSettingsPreparer: InitialSettingsPreparer

    @Inject
    lateinit var dateTimeProvider: DateTimeProvider

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        ComeEventExtensions.init(dateTimeProvider)
        initialSettingsPreparer.initSettings()
        notificationChannelCreator.initNotifications()
    }

    fun closeApp(activity: Activity) {
        activity.finishAffinity()
        exitProcess(0)
    }
}