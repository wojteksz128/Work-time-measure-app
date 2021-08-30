package net.wojteksz128.worktimemeasureapp.window.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import net.wojteksz128.worktimemeasureapp.R

class SyncFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sync_preferences, rootKey)
    }
}