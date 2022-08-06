package net.wojteksz128.worktimemeasureapp.window.settings.property

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
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

    private val coverColorFilter = getCoverColorFilter()

    private fun getCoverColorFilter() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            BlendModeColorFilter(android.R.color.transparent, BlendMode.CLEAR)
        else PorterDuffColorFilter(android.R.color.transparent, PorterDuff.Mode.CLEAR)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        icon.colorFilter = coverColorFilter

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
        icon.clearColorFilter()
        if (icon is AnimatedVectorDrawable) {
            (icon as AnimatedVectorDrawable).start()
        }
        if (icon is AnimatedVectorDrawableCompat) {
            (icon as AnimatedVectorDrawableCompat).start()
        }
    }

    private fun onStopAsyncAction() {
        icon.colorFilter = coverColorFilter
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