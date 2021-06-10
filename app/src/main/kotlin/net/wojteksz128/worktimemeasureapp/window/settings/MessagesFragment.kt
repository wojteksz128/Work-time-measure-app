package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import net.wojteksz128.worktimemeasureapp.R

class MessagesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.messages_preferences, rootKey)
    }
}