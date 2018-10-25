package net.wojteksz128.worktimemeasureapp.window.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import net.wojteksz128.worktimemeasureapp.database.AppDatabase;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayDao;
import net.wojteksz128.worktimemeasureapp.database.workDay.WorkDayEvents;
import net.wojteksz128.worktimemeasureapp.util.PeriodicOperationRunner;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private final LiveData<List<WorkDayEvents>> workDays;
    private final PeriodicOperationRunner<WorkDayEvents> secondRunner;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "ctor: Retrieve work days with events");
        final WorkDayDao workDayDao = AppDatabase.getInstance(application).workDayDao();
        this.workDays = workDayDao.findAllInLiveData();
        this.secondRunner = new PeriodicOperationRunner<>();
    }

    public LiveData<List<WorkDayEvents>> getWorkDays() {
        return this.workDays;
    }

    public PeriodicOperationRunner<WorkDayEvents> getSecondRunner() {
        return this.secondRunner;
    }
}
