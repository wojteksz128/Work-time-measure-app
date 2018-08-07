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

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.ComeEventDao;
import net.wojteksz128.worktimemeasureapp.database.ComeEventType;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private ComeEventDao eventDao;
    private ComeEventAdapter eventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.main_layout);
        FloatingActionButton mEnterFab = findViewById(R.id.enterFab);
        RecyclerView mDayList = findViewById(R.id.main_rv_days);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDayList.setLayoutManager(layoutManager);

        eventAdapter = new ComeEventAdapter();
        mDayList.setAdapter(eventAdapter);

        eventDao = AppDatabase.getInstance(this).comeEventDao();
        LiveData<List<ComeEvent>> eventsData = eventDao.findAllInLiveData();
        eventsData.observe(this, new Observer<List<ComeEvent>>() {
            @Override
            public void onChanged(@Nullable List<ComeEvent> comeEvents) {
                eventAdapter.setEvents(comeEvents);
            }
        });

        mEnterFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ComeEventExecutor.registerNewEvent(MainActivity.this, new Function<ComeEventType, Void>() {
                    @Override
                    public Void apply(ComeEventType input) {
                        // TODO: 2018-08-07 Add loading indicator
                        String message;

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
