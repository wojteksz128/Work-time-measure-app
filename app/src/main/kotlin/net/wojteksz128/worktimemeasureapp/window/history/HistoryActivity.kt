package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.paging.liveData
import androidx.recyclerview.widget.SimpleItemAnimator
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityHistoryBinding
import net.wojteksz128.worktimemeasureapp.util.ClassTagAware
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.window.BaseActivity

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
class HistoryActivity : BaseActivity<ActivityHistoryBinding>(R.layout.activity_history), ClassTagAware {
    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var workDayAdapter: WorkDayAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initWorkDaysRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        Log.v(classTag, "onResume: Fill days list")
        viewModel.workDaysPager.liveData.observe(this,
            { workDayAdapter.submitData(this.lifecycle, it) })
        // TODO: 21.09.2021 Przenieś do innego miesca (niezależnego od HistoryActivity)
        DateTimeProvider.updateOffset(this)
    }

    private fun initWorkDaysRecyclerView() {
        workDayAdapter = WorkDayAdapter()

        binding.historyRvDays.adapter = workDayAdapter
        (binding.historyRvDays.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }
}
