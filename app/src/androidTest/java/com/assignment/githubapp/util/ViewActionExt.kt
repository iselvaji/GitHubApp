package com.assignment.githubapp.util

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import java.util.concurrent.TimeoutException

/**
 * Extension function for View Action to wait until view is loaded
 * and visible while executing UI testing, wait until timeout duration
 *
 */

fun waitUntilVisibleAction() : ViewAction {

    return object : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return any(View::class.java)
        }

        override fun getDescription(): String {
            return "wait for view to become visible"
        }

        override fun perform(uiController: UiController, view: View) {
            val timeout = 15 * 1000

            val endTime = System.currentTimeMillis() + timeout

            do {
                if (view.visibility == View.VISIBLE) return
                uiController.loopMainThreadForAtLeast(50)
            } while (System.currentTimeMillis() < endTime)

            throw PerformException.Builder()
                .withActionDescription(description)
                .withCause(TimeoutException("Waited $timeout milliseconds"))
                .withViewDescription(HumanReadables.describe(view))
                .build()
        }
    }
}

