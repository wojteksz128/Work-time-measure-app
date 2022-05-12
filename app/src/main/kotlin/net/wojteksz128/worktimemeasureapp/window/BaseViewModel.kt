package net.wojteksz128.worktimemeasureapp.window

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.WorkTimeMeasureApp
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.settings.item.StringSettingsItem
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
open class BaseViewModel @Inject constructor(
    application: Application,
    private val Settings: Settings
) : AndroidViewModel(application) {
    val profileImageBitmap: LiveData<Bitmap>
        get() = _profileImageBitmap
    val profileUsername: LiveData<String>
        get() = _profileUsername
    val isProfileUsernameDefined: LiveData<Boolean>
        get() = _isProfileUsernameDefined
    val profileEmail: LiveData<String>
        get() = _profileEmail
    val isProfileEmailDefined: LiveData<Boolean>
        get() = _isProfileEmailDefined

    private val _profileImageBitmap = MutableLiveData<Bitmap>()
    private val _profileUsername = MutableLiveData<String>()
    private val _isProfileUsernameDefined = MutableLiveData(false)
    private val _profileEmail = MutableLiveData<String>()
    private val _isProfileEmailDefined = MutableLiveData(false)

    init {
        viewModelScope.launch {
            loadImage()

            setStringField(
                    Settings.Profile.Username,
                    _profileUsername::setValue,
                    R.string.base_navbar_header_profile_username_notSetMessage,
                    _isProfileUsernameDefined::setValue
            )
            setStringField(
                    Settings.Profile.Email,
                    _profileEmail::setValue,
                    R.string.base_navbar_header_profile_email_notSetMessage,
                    _isProfileEmailDefined::setValue
            )

        }
    }


    private suspend fun loadImage() {
        var imageBitmap: Bitmap?
        var imagePath: String?
        withContext(Dispatchers.IO) {
            imagePath = Settings.Profile.ImagePath.valueNullable
            imageBitmap = if (imagePath != null) {
                BitmapFactory.decodeFile(imagePath)
            } else {
                BitmapFactory.decodeResource(getApplication<WorkTimeMeasureApp>().applicationContext.resources,
                    R.mipmap.ic_launcher_round)
            }
        }
        imageBitmap?.let {
            _profileImageBitmap.value = it
        }
    }

    private fun setStringField(
            settingsItem: StringSettingsItem,
            textUpdate: KFunction1<String, Unit>,
            defaultMessageResId: Int,
            textDefinedUpdate: KFunction1<Boolean, Unit>
    ) {
        val profileUsernameNullable = settingsItem.valueNullable
        textUpdate(
                profileUsernameNullable
                        ?: getApplication<WorkTimeMeasureApp>().getString(
                                defaultMessageResId
                        )
        )
        textDefinedUpdate(profileUsernameNullable != null)
    }
}

