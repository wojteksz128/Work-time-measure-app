package net.wojteksz128.worktimemeasureapp.window

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.Settings
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    application: Application,
    @Suppress("PrivatePropertyName") private val Settings: Settings,
) : AndroidViewModel(application) {
    val profileImageBitmap = MediatorLiveData<Bitmap>().apply {
        addSource(Settings.Profile.ImagePath.valueLiveData) { imagePath ->
            value = imagePath?.let { BitmapFactory.decodeFile(it) } ?: BitmapFactory.decodeResource(
                getApplication<Application>().applicationContext.resources,
                R.mipmap.ic_launcher_round
            )
        }
    }
    val profileUsername = MediatorLiveData<String>().apply {
        addSource(Settings.Profile.Username.valueLiveData) { username ->
            value = username ?: getApplication<Application>().getString(
                R.string.base_navbar_header_profile_username_notSetMessage
            )
        }
    }
    val isProfileUsernameDefined = MediatorLiveData<Boolean>().apply {
        addSource(Settings.Profile.Username.valueLiveData) { username ->
            value = username != null
        }
    }
    val profileEmail = MediatorLiveData<String>().apply {
        addSource(Settings.Profile.Email.valueLiveData) { email ->
            value = email ?: getApplication<Application>().getString(
                R.string.base_navbar_header_profile_email_notSetMessage
            )
        }
    }
    val isProfileEmailDefined = MediatorLiveData<Boolean>().apply {
        addSource(Settings.Profile.Email.valueLiveData) { email ->
            value = email != null
        }
    }
}

