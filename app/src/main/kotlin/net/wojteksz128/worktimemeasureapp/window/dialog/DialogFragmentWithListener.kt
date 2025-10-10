package net.wojteksz128.worktimemeasureapp.window.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

open class DialogFragmentWithListener<Listener> : DialogFragment() {

    var listener: Listener? = null
}

fun <L, DF : DialogFragmentWithListener<L>> showDialogWithListener(
    dialogFragmentClass: Class<DF>,
    fragmentManager: FragmentManager,
    listener: L,
) {
    val dialog = dialogFragmentClass.getDeclaredConstructor().newInstance()
        .apply { this.listener = listener }
    dialog.show(fragmentManager, dialogFragmentClass.toString())
}