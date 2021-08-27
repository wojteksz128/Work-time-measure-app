package net.wojteksz128.worktimemeasureapp.window

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.settings.Settings
import net.wojteksz128.worktimemeasureapp.window.dashboard.DashboardActivity
import net.wojteksz128.worktimemeasureapp.window.history.HistoryActivity
import net.wojteksz128.worktimemeasureapp.window.settings.SettingsActivity

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var job = Job()
    private var scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    private lateinit var viewModel: BaseViewModel

    private lateinit var activityContent: FrameLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var profileImage: ImageView
    private lateinit var profileUsername: TextView
    private lateinit var profileEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)

        viewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        toolbar = findViewById(R.id.base_toolbar)
        activityContent = findViewById(R.id.base_content)
        drawerLayout = findViewById(R.id.base_drawer_layout)
        navView = findViewById(R.id.base_nav_view)

        initNavBar()
    }

    private fun initNavBar() {
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

        navView.setNavigationItemSelectedListener(this)
        val headerView = navView.getHeaderView(0)

        profileImage = headerView.findViewById(R.id.base_navbar_header_profile_image)
        profileUsername = headerView.findViewById(R.id.base_navbar_header_profile_username)
        profileEmail = headerView.findViewById(R.id.base_navbar_header_profile_email)

        // TODO: 27.08.2021 Zmień sposób ładowania
        scopeForSaving.launch {
            if (viewModel.profileImageBitmap == null) {
                loadImage()
            }
            viewModel.profileImageBitmap.let { profileImage.setImageBitmap(it) }
            viewModel.profileUsername = Settings.Profile.Username.getValue(
                baseContext,
                getString(R.string.base_navbar_header_profile_username_example)
            )
            profileUsername.text = viewModel.profileUsername
            viewModel.profileEmail = Settings.Profile.Email.getValue(
                baseContext,
                getString(R.string.base_navbar_header_profile_email_example)
            )
            profileEmail.text = viewModel.profileEmail
        }
    }

    private suspend fun loadImage() {
        var imageBitmap: Bitmap? = null
        var imagePath: String?
        withContext(Dispatchers.IO) {
            imagePath = Settings.Profile.ImagePath.getValue(baseContext, null)
            imagePath?.let {
                imageBitmap = BitmapFactory.decodeFile(imagePath)
            }
        }
        imageBitmap?.let {
            viewModel.profileImageBitmap = it
        }
    }

    override fun setContentView(layoutResID: Int) {
        activityContent.removeAllViews()
        LayoutInflater.from(this).inflate(layoutResID, activityContent)
    }

    override fun setContentView(view: View?) {
        activityContent.removeAllViews()
        activityContent.addView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        activityContent.removeAllViews()
        activityContent.addView(view)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
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