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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wojteksz128.worktimemeasureapp.util.android.before
import net.wojteksz128.worktimemeasureapp.util.android.fromVersion
import net.wojteksz128.worktimemeasureapp.util.android.onVersion

@BindingMethods
class AsyncActionPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    // TODO: 27.08.2021 popraw sposób odwoływania się do innego scope
    private var job = Job()
    private var scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    lateinit var listener: Listener

    private val coverColorFilter = getCoverColorFilter()

    @Suppress("DEPRECATION")
    private fun getCoverColorFilter() =
        fromVersion(Build.VERSION_CODES.Q) {
            BlendModeColorFilter(context.getColor(android.R.color.transparent), BlendMode.CLEAR)
        } before {
            PorterDuffColorFilter(
                context.resources.getColor(android.R.color.transparent),
                PorterDuff.Mode.CLEAR
            )
        }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        icon?.colorFilter = coverColorFilter

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
        icon?.clearColorFilter()
        onVersion(Build.VERSION_CODES.LOLLIPOP) {
            if (icon is AnimatedVectorDrawable) {
                (icon as AnimatedVectorDrawable).start()
            }
        }
        if (icon is AnimatedVectorDrawableCompat) {
            (icon as AnimatedVectorDrawableCompat).start()
        }
    }

    private fun onStopAsyncAction() {
        icon?.colorFilter = coverColorFilter
        onVersion(Build.VERSION_CODES.LOLLIPOP) {
            if (icon is AnimatedVectorDrawable) {
                (icon as AnimatedVectorDrawable).stop()
            }
        }
        if (icon is AnimatedVectorDrawableCompat) {
            (icon as AnimatedVectorDrawableCompat).stop()
        }
    }

    interface Listener {
        suspend fun onAsyncClick()
    }
}