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
import net.wojteksz128.worktimemeasureapp.database.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.ComeEventType;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private ComeEventAdapter mEventAdapter;
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

        mEventAdapter = new ComeEventAdapter();
        mDayList.setAdapter(mEventAdapter);

        ComeEventDao eventDao = AppDatabase.getInstance(this).comeEventDao();
        LiveData<List<ComeEvent>> eventsData = eventDao.findAllInLiveData();
        eventsData.observe(this, new Observer<List<ComeEvent>>() {
            @Override
            public void onChanged(@Nullable List<ComeEvent> comeEvents) {
                mEventAdapter.setEvents(comeEvents);
            }
        });

        mEnterFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ComeEventExecutor.registerNewEvent(MainActivity.this,
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
                                message = "Zarejestrowano wejście do pracy";
                                break;
                            case COME_OUT:
                                message = "Zarejestrowano wyjście z pracy";
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
