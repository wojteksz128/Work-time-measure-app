package net.wojteksz128.worktimemeasureapp.util

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import net.wojteksz128.worktimemeasureapp.HiltTestActivity
import net.wojteksz128.worktimemeasureapp.R
import dagger.hilt.internal.Preconditions as HiltPreconditions

inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.AppTheme_NoActionBar,
    crossinline preAction: (HiltTestActivity) -> Unit = {},
    crossinline action: T.() -> Unit = {},
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        preAction(activity)

        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            HiltPreconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs

        if (fragment is DialogFragment) {
            fragment.show(activity.supportFragmentManager, "TestDialog")
            activity.supportFragmentManager.executePendingTransactions()
        } else {
            activity.supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, "")
                .commitNow()
        }

        (fragment as T).action()
    }
}