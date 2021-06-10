package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.window.settings.property.ImageViewPreference

class ProfileFragment : PreferenceFragmentCompat() {

    lateinit var imageViewPreference: ImageViewPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.profile_preferences, rootKey)

        imageViewPreference = findPreference("settings_profile_image")!!
        imageViewPreference.imageClickListener = View.OnClickListener {
            // TODO: 10.06.2021  Zmiana zdjęcia
            Toast.makeText(context, "Image Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}