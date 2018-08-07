package net.wojteksz128.worktimemeasureapp;

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
        LiveData<List<ComeEvent>> eventsData = eventDao.findAll();
        eventsData.observe(this, new Observer<List<ComeEvent>>() {
            @Override
            public void onChanged(@Nullable List<ComeEvent> comeEvents) {
                eventAdapter.setEvents(comeEvents);
            }
        });

        mEnterFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ComeEventExecutor.registerNewEvent(MainActivity.this);
                Snackbar.make(mLayout, "Dodano nową pozycję.", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
