package net.wojteksz128.worktimemeasureapp.window.dialog

import android.content.Intent.ACTION_GET_CONTENT
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction

class EditImageRobot {

    fun selectTestImage() {
        Intents.intended(hasAction(ACTION_GET_CONTENT))
        Thread.sleep(500)
    }
}