package net.wojteksz128.worktimemeasureapp.window

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel

class BaseViewModel(application: Application) : AndroidViewModel(application) {

    var profileImageBitmap: Bitmap? = null
    var profileUsername: String? = null
    var profileEmail: String? = null
}
