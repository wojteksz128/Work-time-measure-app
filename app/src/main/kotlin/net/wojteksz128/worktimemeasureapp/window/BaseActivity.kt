package net.wojteksz128.worktimemeasureapp.window

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityBaseBinding
import net.wojteksz128.worktimemeasureapp.databinding.BaseNavHeaderBinding
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import net.wojteksz128.worktimemeasureapp.window.history.HistoryActivity
import net.wojteksz128.worktimemeasureapp.window.settings.SettingsActivity

abstract class BaseActivity<VDB>(@LayoutRes val layoutResId: Int? = null) :
    AppCompatActivity() where VDB : ViewDataBinding {

    private val viewModel: BaseViewModel by viewModels()
    private lateinit var baseBinding: ActivityBaseBinding
    protected lateinit var binding: VDB

    protected val baseContainer: ViewGroup
        get() = baseBinding.baseAppBar.baseContent
    private val baseRoot: DrawerLayout
        get() = baseBinding.baseDrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBindingsAndContentView()
        initActionBar()
        initNavBar()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initBindingsAndContentView()
        initActionBar()
        initNavBar()
    }

    private fun initBindingsAndContentView() {
        baseBinding =
            ActivityBaseBinding.inflate(layoutInflater)
                .apply {
                    this.lifecycleOwner = this@BaseActivity
                    this.menuItemSelectedListener = MenuItemSelectedListener { this@BaseActivity }
                }
        layoutResId?.let {
            binding = DataBindingUtil.inflate(layoutInflater, layoutResId, baseContainer, true)
        }
        setContentView(baseRoot)
    }

    private fun initActionBar() {
        val toolbar = baseBinding.baseAppBar.baseToolbar
        val drawerLayout = baseBinding.baseDrawerLayout

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initNavBar() {
        val headerView = baseBinding.baseNavView.getHeaderView(0) as LinearLayout

        BaseNavHeaderBinding.bind(headerView)
            .apply {
                this.lifecycleOwner = this@BaseActivity
                this.viewModel = this@BaseActivity.viewModel
            }
    }

    override fun onBackPressed() {
        if (baseRoot.isDrawerOpen(GravityCompat.START)) {
            baseRoot.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    class MenuItemSelectedListener<VDB>(val baseActivityGetter: () -> BaseActivity<VDB>) :
        NavigationView.OnNavigationItemSelectedListener where VDB : ViewDataBinding {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(baseActivityGetter(), DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    baseActivityGetter().startActivity(intent)
                }
                R.id.nav_history -> {
                    val intent = Intent(baseActivityGetter(), HistoryActivity::class.java)
                    baseActivityGetter().startActivity(intent)
                }
                R.id.nav_settings -> {
                    val intent = Intent(baseActivityGetter(), SettingsActivity::class.java)
                    baseActivityGetter().startActivity(intent)
                }
                R.id.nav_about -> {
                    Snackbar.make(
                        baseActivityGetter().baseBinding.baseAppBar.baseContent,
                        R.string.about,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            baseActivityGetter().baseBinding.baseDrawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }
}