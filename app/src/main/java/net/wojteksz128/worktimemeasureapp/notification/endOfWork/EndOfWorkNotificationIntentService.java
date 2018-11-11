package net.wojteksz128.worktimemeasureapp.notification.endOfWork;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class EndOfWorkNotificationIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public EndOfWorkNotificationIntentService() {
        super(EndOfWorkNotificationIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final String action = intent.getAction();
        EndOfWorkNotification.Action.valueOf(action).doAction(this);
    }
}
