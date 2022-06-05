package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingMethods
import androidx.preference.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@BindingMethods
class AsyncActionPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    // TODO: 27.08.2021 popraw sposób odwoływania się do innego scope
    private var job = Job()
    private var scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    lateinit var listener: Listener

    init {
        setOnPreferenceClickListener {
            scopeForSaving.launch {
                // TODO: Add icon on async operation in progress
                listener.onAsyncClick()
                // TODO: Remove icon on async operation in progress
            }
            true
        }
    }

    interface Listener {
        suspend fun onAsyncClick()
    }
}