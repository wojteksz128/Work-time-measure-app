package net.wojteksz128.worktimemeasureapp.window.main;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import net.wojteksz128.worktimemeasureapp.R;
import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;
import net.wojteksz128.worktimemeasureapp.util.ComeEventUtils;

import java.util.List;

// TODO: 09.08.2018 Dodaj joba, który automatycznie zamknie dzień pracy o godzinie zmiany dnia pracy
// TODO: 11.08.2018 Dodaj wątek, który będzie automatycznie zmieniać sekundy, gdy widzi się czas i leci czas pracy
// TODO: 11.08.2018 Jeśli aktualny dzień istnieje - przenieś FABa w to miejsce
// TODO: 11.08.2018 Dodaj statystyki
// TODO: 11.08.2018 Dodaj notyfikację na kilka minut przed wyjściem z pracy
// TODO: 11.08.2018 Dodaj konfigurację
// TODO: 11.08.2018 dodaj możliwość importu eventów
// TODO: 11.08.2018 Dodaj możliwość edycji istniejących eventów lub ich usunięcia (części lub całości)
// TODO: 11.08.2018 Dodaj widok kalendarza
// TODO: 11.08.2018 dodaj drawer layout (hamburger)
// TODO: 11.08.2018 popraw liczenie czasu pracy (może nie brać pod uwagę ms?)
public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private WorkDayAdapter mWorkDayAdapter;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.main_layout);
        mLoadingIndicator = findViewById(R.id.main_loading_indicator);

        initWorkDaysRecyclerView();
        initFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillDayList();
    }

    private void initWorkDaysRecyclerView() {
        mWorkDayAdapter = new WorkDayAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView mDayList = findViewById(R.id.main_rv_days);
        mDayList.setLayoutManager(layoutManager);
        mDayList.setAdapter(mWorkDayAdapter);
    }

    private void fillDayList() {
        final WorkDayDao workDayDao = AppDatabase.getInstance(this).workDayDao();
        final LiveData<List<WorkDayEvents>> workDayData = workDayDao.findAllInLiveData();
        workDayData.observe(this, new Observer<List<WorkDayEvents>>() {
            @Override
            public void onChanged(@Nullable List<WorkDayEvents> workDayEvents) {
                mWorkDayAdapter.setWorkDays(workDayEvents);
            }
        });
    }

    private void initFab() {
        FloatingActionButton mEnterFab = findViewById(R.id.main_enter_fab);
        mEnterFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ComeEventUtils.registerNewEvent(MainActivity.this,
                        new Function<Void, Void>() {
                            @Override
                            public Void apply(Void input) {
                                mLoadingIndicator.setVisibility(View.VISIBLE);
                                return null;
                            }
                        },
                        new Function<ComeEventType, Void>() {
                            @Override
                            public Void apply(ComeEventType input) {
                                String message;

                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                switch (input) {
                                    case COME_IN:
                                        message = getString(R.string.main_snackbar_info_income_registered);
                                        break;
                                    case COME_OUT:
                                        message = getString(R.string.main_snackbar_info_outcome_registered);
                                        break;
                                    default:
                                        message = "Incorrect event type";
                                }

                                Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG).show();
                                return null;
                            }
                        });

            }
        });
    }
}
