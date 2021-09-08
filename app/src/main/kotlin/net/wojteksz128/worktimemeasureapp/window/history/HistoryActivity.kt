package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.paging.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider

// TODO: 09.08.2018 Dodaj joba, który automatycznie zamknie dzień pracy o godzinie zmiany dnia pracy
// TODO: 11.08.2018 Jeśli aktualny dzień istnieje - przenieś FABa w to miejsce
// TODO: 11.08.2018 Dodaj statystyki
// TODO: 11.08.2018 Dodaj notyfikację na kilka minut przed wyjściem z pracy
// TODO: 11.08.2018 dodaj możliwość importu eventów
// TODO: 11.08.2018 Dodaj możliwość edycji istniejących eventów lub ich usunięcia (części lub całości)
// TODO: 11.08.2018 Dodaj widok kalendarza
// TODO: 11.08.2018 popraw liczenie czasu pracy (może nie brać pod uwagę ms?)
// TODO: 07.07.2019 Uwzględniaj strefę czasową
// TODO: 09.06.2021 Uwzględnij przejścia poza jeden dzień oraz możliwość zamknięcia
class HistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var layout: ConstraintLayout
    private lateinit var workDayAdapter: WorkDayAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        Log.v(TAG, "onCreate: Create or get HistoryViewModel object")
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        layout = findViewById(R.id.history_layout)

        initWorkDaysRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume: Fill days list")
        viewModel.workDaysPager.liveData.observe(this,
            { workDayAdapter.submitData(this.lifecycle, it) })
        DateTimeProvider.updateOffset(this)
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause: Stop second updater")
        this.viewModel.secondRunner.stop()
    }

    private fun initWorkDaysRecyclerView() {
        workDayAdapter = WorkDayAdapter { action: Runnable -> runOnUiThread(action) }

        val layoutManager = LinearLayoutManager(this)

        val mDayList = findViewById<RecyclerView>(R.id.history_rv_days)
        mDayList.layoutManager = layoutManager
        mDayList.adapter = workDayAdapter
        (mDayList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    companion object {
        private val TAG = HistoryActivity::class.java.simpleName
    }
}
