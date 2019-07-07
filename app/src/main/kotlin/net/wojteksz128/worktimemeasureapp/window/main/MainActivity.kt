package net.wojteksz128.worktimemeasureapp.window.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents
import net.wojteksz128.worktimemeasureapp.util.ComeEventUtils
import net.wojteksz128.worktimemeasureapp.util.FunctionWithParameter

// TODO: 09.08.2018 Dodaj joba, który automatycznie zamknie dzień pracy o godzinie zmiany dnia pracy
// TODO: 11.08.2018 Jeśli aktualny dzień istnieje - przenieś FABa w to miejsce
// TODO: 11.08.2018 Dodaj statystyki
// TODO: 11.08.2018 Dodaj notyfikację na kilka minut przed wyjściem z pracy
// TODO: 11.08.2018 Dodaj konfigurację
// TODO: 11.08.2018 dodaj możliwość importu eventów
// TODO: 11.08.2018 Dodaj możliwość edycji istniejących eventów lub ich usunięcia (części lub całości)
// TODO: 11.08.2018 Dodaj widok kalendarza
// TODO: 11.08.2018 dodaj drawer layout (hamburger)
// TODO: 11.08.2018 popraw liczenie czasu pracy (może nie brać pod uwagę ms?)
// TODO: 07.07.2019 Uwzględniaj strefę czasową
class MainActivity : AppCompatActivity() {
    private var mainViewModel: MainViewModel? = null
    private var mLayout: ConstraintLayout? = null
    private var mWorkDayAdapter: WorkDayAdapter? = null
    private var mLoadingIndicator: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v(TAG, "onCreate: Create or get MainViewModel object")
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mLayout = findViewById(R.id.main_layout)
        mLoadingIndicator = findViewById(R.id.main_loading_indicator)

        initWorkDaysRecyclerView()
        initFab()
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume: Fill days list")
        mainViewModel!!.workDays.observe(this, DayListObserver())
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause: Stop second updater")
        this.mainViewModel!!.secondRunner.stop()
    }

    private fun initWorkDaysRecyclerView() {
        mWorkDayAdapter = WorkDayAdapter()

        val layoutManager = LinearLayoutManager(this)

        val mDayList = findViewById<RecyclerView>(R.id.main_rv_days)
        mDayList.layoutManager = layoutManager
        mDayList.adapter = mWorkDayAdapter
        (mDayList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun initFab() {
        val mEnterFab = findViewById<FloatingActionButton>(R.id.main_enter_fab)
        mEnterFab.setOnClickListener {
            ComeEventUtils.registerNewEvent(this@MainActivity,
                    {
                        mLoadingIndicator!!.visibility = View.VISIBLE
                    },
                    { input ->
                        val message: String = when (input) {
                            ComeEventType.COME_IN -> getString(R.string.main_snackbar_info_income_registered)
                            ComeEventType.COME_OUT -> getString(R.string.main_snackbar_info_outcome_registered)
                        }

                        mLoadingIndicator!!.visibility = View.INVISIBLE

                        Snackbar.make(mLayout!!, message, Snackbar.LENGTH_LONG).show()
                    })
        }
    }

    private inner class DayListObserver : Observer<List<WorkDayEvents>> {

        private val TAG = DayListObserver::class.java.simpleName
        // FIXME: 25.10.2018 This way is not correct - fix it in the mean time
        private val LAST_SAVED_DAY = 0


        override fun onChanged(workDayEvents: List<WorkDayEvents>?) {
            workDayEvents?.let {
                mWorkDayAdapter!!.setWorkDays(it)

                val currentDayEvents = workDayEvents[LAST_SAVED_DAY]
                if (!currentDayEvents.hasEventsEnded()) {
                    if (!mainViewModel!!.secondRunner.isRunning) {
                        mainViewModel!!.secondRunner.setConsumer(getUpdateAction(currentDayEvents))
                        Log.v(TAG, "onChanged: start second updater")
                        mainViewModel!!.secondRunner.start()
                    }
                } else {
                    mainViewModel!!.secondRunner.stop()
                }
            }
        }

        private fun getUpdateAction(currentDayEvents: WorkDayEvents): FunctionWithParameter<WorkDayEvents> {
            return object : FunctionWithParameter<WorkDayEvents>(currentDayEvents) {

                override fun action(obj: WorkDayEvents) {
                    Log.v(TAG, "onChanged: Update work day")
                    runOnUiThread { mWorkDayAdapter!!.notifyItemChanged(0) }
                }
            }
        }
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }
}
