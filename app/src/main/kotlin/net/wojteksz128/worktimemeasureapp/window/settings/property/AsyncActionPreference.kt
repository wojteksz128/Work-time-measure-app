package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import androidx.databinding.BindingMethods
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.coroutines.*

@BindingMethods
class AsyncActionPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    // TODO: 27.08.2021 popraw sposób odwoływania się do innego scope
    private var job = Job()
    private var scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    lateinit var listener: Listener

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        if (icon is AnimatedVectorDrawableCompat) {
            (icon as AnimatedVectorDrawableCompat).start()
        }

        setOnPreferenceClickListener {
            scopeForSaving.launch {
                withContext(Dispatchers.Main) { onStartAsyncAction() }
                listener.onAsyncClick()
                withContext(Dispatchers.Main) { onStopAsyncAction() }
            }
            true
        }
    }

    private fun onStartAsyncAction() {
        if (icon is AnimatedVectorDrawable) {
            (icon as AnimatedVectorDrawable).start()
        }
        if (icon is AnimatedVectorDrawableCompat) {
            (icon as AnimatedVectorDrawableCompat).start()
        }
    }

    private fun onStopAsyncAction() {
        if (icon is AnimatedVectorDrawable) {
            (icon as AnimatedVectorDrawable).stop()
        }
        if (icon is AnimatedVectorDrawableCompat) {
            (icon as AnimatedVectorDrawableCompat).stop()
        }
    }

    interface Listener {
        suspend fun onAsyncClick()
    }
}