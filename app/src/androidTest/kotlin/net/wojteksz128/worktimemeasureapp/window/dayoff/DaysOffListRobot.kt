package net.wojteksz128.worktimemeasureapp.window.dayoff

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag

class DaysOffListRobot(private val composeTestRule: ComposeTestRule) {

    fun verifyIsDisplayed() {
        composeTestRule.onNodeWithTag("days_off_list_layout").assertIsDisplayed()
    }
}