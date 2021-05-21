package net.wojteksz128.worktimemeasureapp.window

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = BaseViewModel::class.java.simpleName
}
