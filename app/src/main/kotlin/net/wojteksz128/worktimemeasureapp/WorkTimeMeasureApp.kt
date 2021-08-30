package net.wojteksz128.worktimemeasureapp

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

class WorkTimeMeasureApp : Application() {

    override fun onCreate() {
        super.onCreate()
        context = WeakReference(applicationContext)
    }

    companion object {
        lateinit var context: WeakReference<Context>
    }
}