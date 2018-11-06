package net.wojteksz128.worktimemeasureapp.notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class EndOfWorkIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public EndOfWorkIntentService() {
        super(EndOfWorkIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final String action = intent.getAction();
        EndOfWorkNotification.Action.valueOf(action).doAction(this);
    }
}
