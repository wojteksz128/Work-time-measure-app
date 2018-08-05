package net.wojteksz128.worktimemeasureapp;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.wojteksz128.worktimemeasureapp.database.ComeEvent;
import net.wojteksz128.worktimemeasureapp.database.ComeEventType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private FloatingActionButton mEnterFab;
    private RecyclerView mDayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.main_layout);
        mEnterFab = findViewById(R.id.enterFab);
        mDayList = findViewById(R.id.main_rv_days);

        mEnterFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(mLayout, "Dodano nową pozycję.", Snackbar.LENGTH_LONG).show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDayList.setLayoutManager(layoutManager);

        final ComeEventAdapter eventAdapter = new ComeEventAdapter(Arrays.asList(new ComeEvent(new Date(), ComeEventType.COME_IN), new ComeEvent(new Date(), ComeEventType.COME_OUT)));
        mDayList.setAdapter(eventAdapter);
    }
}
