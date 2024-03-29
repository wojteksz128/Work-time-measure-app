package net.wojteksz128.worktimemeasureapp.window.dashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.util.FunctionWithParameter
import net.wojteksz128.worktimemeasureapp.window.history.ComeEventViewHolder
import net.wojteksz128.worktimemeasureapp.window.history.HistoryActivity
import java.util.*

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = DashboardActivity::class.java.simpleName
    private lateinit var viewModel: DashboardViewModel

    private lateinit var layout: View
    private lateinit var remainingDayTime: TextView
    private lateinit var remainingWeekTime: TextView
    private lateinit var todayWorkTime: TextView
    private lateinit var currentDayDateLabel: TextView
    private lateinit var currentDayEvents: LinearLayout
    private lateinit var currentDayEmptyEventsLabel: TextView
    private lateinit var loadingIndicator: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        Log.v(TAG, "onCreate: Create or get DashboardViewModel object")
        viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        layout = findViewById(R.id.dashboard_content)
        remainingDayTime = findViewById(R.id.dashboard_remaining_day_time)
        remainingWeekTime = findViewById(R.id.dashboard_remaining_week_time)
        todayWorkTime = findViewById(R.id.dashboard_today_work_time)
        currentDayDateLabel = findViewById(R.id.dashboard_current_day_date)
        currentDayEvents = findViewById(R.id.dashboard_current_day_events_list)
        currentDayEmptyEventsLabel = findViewById(R.id.dashboard_current_day_empty_events_message)
        loadingIndicator = findViewById(R.id.dashboard_loading_indicator)

        initFab()
        initNavBar()
    }

    private lateinit var currentDayObserver: CurrentDayObserver

    private lateinit var lastWeekObserver: LastWeekObserver

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Fill days list")
        currentDayObserver = CurrentDayObserver()
        lastWeekObserver = LastWeekObserver()
        viewModel.workDay.observe(this, currentDayObserver)
        viewModel.weekWorkDays.observe(this, lastWeekObserver)
        DateTimeProvider.updateOffset(this, "ntp.comarch.pl")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Stop second updater")
        this.viewModel.secondRunner.stop()
    }

    private fun initFab() {
        val enterFab: FloatingActionButton = findViewById(R.id.dashboard_enter_fab)
        enterFab.setOnClickListener {
            ComeEventUtils.registerNewEvent(this@DashboardActivity,
                    {
                        loadingIndicator.visibility = View.VISIBLE
                    },
                    { input ->
                        val message: String = when (input) {
                            ComeEventType.COME_IN -> getString(R.string.dashboard_snackbar_info_income_registered)
                            ComeEventType.COME_OUT -> getString(R.string.dashboard_snackbar_info_outcome_registered)
                        }

                        loadingIndicator.visibility = View.INVISIBLE

                        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show()
                    })
        }
    }

    private fun initNavBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.dashboard_nav_view)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
            R.id.nav_about -> {
                Snackbar.make(findViewById(R.id.dashboard_content), R.string.about, Snackbar.LENGTH_LONG).show()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private inner class CurrentDayObserver : Observer<WorkDayEvents> {
        private val TAG = CurrentDayObserver::class.java.simpleName

        override fun onChanged(workDayEvents: WorkDayEvents?) {
            workDayEvents?.let {
                fillRemainingWorkDayTime(it)
                fillTodayWorkTime(it)
                fillTodayEvents(it)

                if (!it.hasEventsEnded()) {
                    if (!viewModel.secondRunner.isRunning) {
                        viewModel.secondRunner.setConsumer(getUpdateAction(it))
                        Log.v(TAG, "onChanged: start second updater")
                        viewModel.secondRunner.start()
                    }
                }
            }
        }

        private fun fillRemainingWorkDayTime(it: WorkDayEvents) {
            val elapsedTime = Date(Date(30600000).time - DateTimeUtils.mergeComeEventsDuration(it).time)
            val formatDate = DateTimeUtils.formatDate(getString(R.string.history_work_day_duration_format), elapsedTime, TimeZone.getTimeZone("UTC"))
            remainingDayTime.text = formatDate
        }

        private fun fillTodayWorkTime(it: WorkDayEvents) {
            val formatDate = DateTimeUtils.formatDate(getString(R.string.history_work_day_duration_format), DateTimeUtils.mergeComeEventsDuration(it), TimeZone.getTimeZone("UTC"))
            todayWorkTime.text = formatDate
        }

        private fun fillTodayEvents(it: WorkDayEvents) {
            val formatDate = DateTimeUtils.formatDate(getString(R.string.history_work_day_label_format), it.workDay.date)
            currentDayDateLabel.text = formatDate

            if (it.events.isEmpty()) {
                currentDayEvents.visibility = View.INVISIBLE
                currentDayEmptyEventsLabel.visibility = View.VISIBLE
            } else {
                currentDayEvents.visibility = View.VISIBLE
                currentDayEmptyEventsLabel.visibility = View.INVISIBLE

                val inflater = LayoutInflater.from(this@DashboardActivity)
                currentDayEvents.removeAllViews()

                it.events.forEach {
                    val eventViewHolder = ComeEventViewHolder(inflater.inflate(R.layout.history_day_event_list_item, currentDayEvents, false))
                    eventViewHolder.bind(it)
                    currentDayEvents.addView(eventViewHolder.view)
                }
            }
        }

        private fun getUpdateAction(currentDay: WorkDayEvents): FunctionWithParameter<WorkDayEvents> {
            return object : FunctionWithParameter<WorkDayEvents>(currentDay) {

                override fun action(obj: WorkDayEvents) {
                    Log.v(TAG, "onChanged: Update work day")
                    runOnUiThread {
                        lastWeekObserver.onChanged(viewModel.weekWorkDays.value)
                        currentDayObserver.onChanged(viewModel.workDay.value)
                    }
                }

            }
        }

    }

    private inner class LastWeekObserver : Observer<PagedList<WorkDayEvents>> {
        private val TAG = LastWeekObserver::class.java.simpleName

        override fun onChanged(t: PagedList<WorkDayEvents>?) {
            t?.let {
                fillRemainingWeekWorkDayTime(it)
            }
        }

        private fun fillRemainingWeekWorkDayTime(it: PagedList<WorkDayEvents>) {
            val elapsedTime = Date(Date(153000000).time - it.sumBy { DateTimeUtils.mergeComeEventsDuration(it).time.toInt() })
            val formatDate = DateTimeUtils.formatDate(getString(R.string.history_work_day_duration_format), elapsedTime, TimeZone.getTimeZone("UTC"))
            remainingWeekTime.text = formatDate
        }

    }
}
