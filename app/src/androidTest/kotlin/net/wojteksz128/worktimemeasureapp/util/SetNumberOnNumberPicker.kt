package net.wojteksz128.worktimemeasureapp.util

import android.view.View
import android.widget.NumberPicker
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher
import kotlin.math.abs

fun setNumberOnNumberPicker(value: Int): ViewAction = SetNumberOnNumberPicker(value)

class SetNumberOnNumberPicker(val value: Int) : ViewAction {
    companion object {
        const val ROWS_PER_SWIPE = 5
    }

    override fun perform(uiController: UiController, view: View) {
        val numberPicker = view as NumberPicker
        val numberPickerValueRangeSize = numberPicker.maxValue - numberPicker.minValue + 1

        while (numberPicker.value != value) {
            val normalDelta = abs(value - numberPicker.value)
            val reverseDelta = numberPickerValueRangeSize - normalDelta

            val (delta, reverse) = if (normalDelta <= reverseDelta) Pair(normalDelta, false)
            else Pair(reverseDelta, true)

            if (delta >= ROWS_PER_SWIPE) {
                performSwipe(numberPicker, reverse, uiController, view)
            } else {
                performSelectNeighbor(numberPicker, reverse, uiController, view)
            }
        }
    }

    private fun performSwipe(
        numberPicker: NumberPicker,
        reverse: Boolean,
        uiController: UiController,
        view: NumberPicker,
    ) {
        val (directionFrom, directionTo) = if (value > numberPicker.value) if (reverse) Pair(
            GeneralLocation.TOP_CENTER,
            GeneralLocation.BOTTOM_CENTER
        )
        else Pair(GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER)
        else if (reverse) Pair(
            GeneralLocation.BOTTOM_CENTER,
            GeneralLocation.TOP_CENTER
        )
        else Pair(GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER)

        GeneralSwipeAction(
            Swipe.FAST,
            directionFrom,
            directionTo,
            Press.FINGER
        ).perform(uiController, view)
    }

    private fun performSelectNeighbor(
        numberPicker: NumberPicker,
        reverse: Boolean,
        uiController: UiController,
        view: NumberPicker,
    ) {
        val direction =
            if (value > numberPicker.value) if (reverse) GeneralLocation.TOP_CENTER else GeneralLocation.BOTTOM_CENTER
            else if (reverse) GeneralLocation.BOTTOM_CENTER else GeneralLocation.TOP_CENTER
        GeneralClickAction(Tap.SINGLE, direction, Press.FINGER, 0, 0).perform(
            uiController,
            view
        )
    }

    override fun getDescription(): String = "Set value on NumberPicker by tapping"

    override fun getConstraints(): Matcher<View> =
        isAssignableFrom(NumberPicker::class.java)
}