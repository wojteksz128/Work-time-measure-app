package net.wojteksz128.worktimemeasureapp.notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import net.wojteksz128.worktimemeasureapp.notification.endOfWork.EndOfWorkNotification;

public class NotificationIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationIntentService() {
        super(NotificationIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final String action = intent.getAction();
        EndOfWorkNotification.Action.valueOf(action).doAction(this);
    }
}
