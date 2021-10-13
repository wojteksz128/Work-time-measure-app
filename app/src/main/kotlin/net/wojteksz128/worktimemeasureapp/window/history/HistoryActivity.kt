package net.wojteksz128.worktimemeasureapp.window.history

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.wojteksz128.worktimemeasureapp.R
import net.wojteksz128.worktimemeasureapp.databinding.ActivityHistoryBinding
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeProvider
import net.wojteksz128.worktimemeasureapp.util.datetime.DateTimeUtils
import net.wojteksz128.worktimemeasureapp.window.BaseActivity
import javax.inject.Inject

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

@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>(R.layout.activity_history) {
    @Inject
    lateinit var dateTimeProvider: DateTimeProvider
    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(binding.historyFragmentMainContainer.id, WorkDaysHistoryFragment(dateTimeUtils))
            .commit()
    }

    override fun onResume() {
        super.onResume()

        // TODO: 21.09.2021 Przenieś do innego miesca (niezależnego od HistoryActivity)
        dateTimeProvider.updateOffset(this)
    }
}
