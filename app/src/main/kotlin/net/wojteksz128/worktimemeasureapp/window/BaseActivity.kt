package net.wojteksz128.worktimemeasureapp.window

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
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

abstract class BaseActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by viewModels()
    private var activityContent: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityBaseBinding =
            DataBindingUtil.setContentView<ActivityBaseBinding>(this, R.layout.activity_base)
                .apply {
                    this.lifecycleOwner = this@BaseActivity
                    this.menuItemSelectedListener = MenuItemSelectedListener()
                }

        activityContent = findViewById(R.id.base_content)

        initActionBar()
        initNavBar(activityBaseBinding)
    }

    private fun initActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.base_toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.base_drawer_layout)

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

    private fun initNavBar(activityBaseBinding: ActivityBaseBinding) {
        val headerView = activityBaseBinding.baseNavView.getHeaderView(0) as LinearLayout

        BaseNavHeaderBinding.bind(headerView)
            .apply {
                this.lifecycleOwner = this@BaseActivity
                this.viewModel = this@BaseActivity.viewModel
            }
    }

    override fun setContentView(layoutResID: Int) {
        if (activityContent != null) {
            activityContent!!.removeAllViews()
            LayoutInflater.from(this).inflate(layoutResID, activityContent)
        } else {
            super.setContentView(layoutResID)
        }
    }

    override fun setContentView(view: View?) {
        if (activityContent != null) {
            activityContent!!.removeAllViews()
            activityContent!!.addView(view)
        } else {
            super.setContentView(view)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (activityContent != null) {
            activityContent!!.removeAllViews()
            activityContent!!.addView(view)
        } else {
            super.setContentView(view, params)
        }
    }

    inner class MenuItemSelectedListener : NavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this@BaseActivity, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                R.id.nav_history -> {
                    val intent = Intent(this@BaseActivity, HistoryActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    val intent = Intent(this@BaseActivity, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_about -> {
                    Snackbar.make(
                        findViewById(R.id.dashboard_content),
                        R.string.about,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            val drawerLayout: DrawerLayout = findViewById(R.id.base_drawer_layout)
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }
}