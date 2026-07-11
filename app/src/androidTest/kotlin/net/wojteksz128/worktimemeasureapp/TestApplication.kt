package net.wojteksz128.worktimemeasureapp

import android.app.Application
import dagger.hilt.android.EarlyEntryPoints
import net.wojteksz128.worktimemeasureapp.di.TestBindsEntryPoint
import net.wojteksz128.worktimemeasureapp.util.model.extension.ComeEventExtensions

open class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val dataTimeProvider =
            EarlyEntryPoints.get(this, TestBindsEntryPoint::class.java)
                .getDateTimeProvider()

        ComeEventExtensions.init(dataTimeProvider)
    }
}
