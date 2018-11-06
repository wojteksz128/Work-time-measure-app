package net.wojteksz128.worktimemeasureapp.notification.endOfWork;

import android.content.Context;
import android.widget.Toast;

import net.wojteksz128.worktimemeasureapp.notification.NotificationAction;

class EndOfWorkAction implements NotificationAction {

    @Override
    public void invoke(Context context) {
        // TODO: 03.11.2018 implement this
        Toast.makeText(context, "EndOfWorkAction", Toast.LENGTH_LONG).show();
    }
}
