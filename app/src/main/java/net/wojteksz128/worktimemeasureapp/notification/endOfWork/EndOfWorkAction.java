package net.wojteksz128.worktimemeasureapp.notification.endOfWork;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import net.wojteksz128.worktimemeasureapp.R;
import net.wojteksz128.worktimemeasureapp.notification.NotificationAction;
import net.wojteksz128.worktimemeasureapp.notification.NotificationUtils;

class EndOfWorkAction implements NotificationAction {

    @Override
    public void invoke(final Context context) {
        // TODO: 03.11.2018 implement this
        NotificationUtils.clearAllNotifications(context);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.main_snackbar_info_outcome_registered), Toast.LENGTH_LONG).show();
            }
        });
    }
}
