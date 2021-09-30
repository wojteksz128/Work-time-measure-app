package net.wojteksz128.worktimemeasureapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils
import java.lang.ref.WeakReference

@HiltAndroidApp
class WorkTimeMeasureApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = WeakReference(applicationContext)

        NotificationUtils.initNotifications(this)
    }

    companion object {
        // TODO: 30.09.2021 use hilt
        private lateinit var appContext: WeakReference<Context>
        val context: Context
            get() = appContext.get()
                ?: throw NullPointerException("Cannot get application context!")
    }
}