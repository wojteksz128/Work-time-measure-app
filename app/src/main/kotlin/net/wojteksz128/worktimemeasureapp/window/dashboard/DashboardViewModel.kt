package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    init {
        Log.d(TAG, "ctor: Retrieve fields")
    }

    companion object {
        private val TAG = DashboardViewModel::class.java.simpleName
    }
}
