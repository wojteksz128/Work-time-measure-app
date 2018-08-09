package net.wojteksz128.worktimemeasureapp;

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

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.comeEvent.ComeEventType;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;
import net.wojteksz128.worktimemeasureapp.util.ComeEventUtils;

import java.util.List;

// TODO: 09.08.2018 Dodaj joba, który automatycznie zamknie dzień pracy o godzinie zmiany dnia pracy

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
        FloatingActionButton mEnterFab = findViewById(R.id.main_enter_fab);
        RecyclerView mDayList = findViewById(R.id.main_rv_days);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDayList.setLayoutManager(layoutManager);

        mWorkDayAdapter = new WorkDayAdapter();
        mDayList.setAdapter(mWorkDayAdapter);

        final WorkDayDao workDayDao = AppDatabase.getInstance(this).workDayDao();
        final LiveData<List<WorkDayEvents>> workDayData = workDayDao.findAllInLiveData();
        workDayData.observe(this, new Observer<List<WorkDayEvents>>() {
            @Override
            public void onChanged(@Nullable List<WorkDayEvents> workDayEvents) {
                mWorkDayAdapter.setWorkDays(workDayEvents);
            }
        });

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
