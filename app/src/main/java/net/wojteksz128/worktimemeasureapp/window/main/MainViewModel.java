package net.wojteksz128.worktimemeasureapp.window.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<WorkDayEvents>> workDays;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "Retrieve work days with events");
        final WorkDayDao workDayDao = AppDatabase.getInstance(application).workDayDao();
        this.workDays = workDayDao.findAllInLiveData();
    }

    public LiveData<List<WorkDayEvents>> getWorkDays() {
        return this.workDays;
    }
}
