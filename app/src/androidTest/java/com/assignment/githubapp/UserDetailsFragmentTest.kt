package com.assignment.githubapp

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.assignment.githubapp.util.launchFragmentInHiltContainer
import com.assignment.githubapp.util.waitUntilVisibleAction
import com.assignment.githubapp.view.MainActivity
import com.assignment.githubapp.view.UserDetailsFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Test class which contains the test cases for User details UI
 *
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
class UserDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun test_details_fragment_success() {

        val args = bundleOf("user" to "JakeWharton")

        launchFragmentInHiltContainer<UserDetailsFragment>(
            themeResId = R.style.Theme_GitHubApp,
            fragmentArgs = args
        )

        onView(withId(R.id.txtLoginId)).perform(waitUntilVisibleAction())

        onView(withId(R.id.textViewError))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.txtLoginId)).check(matches(isDisplayed()))

        onView(withId(R.id.txtLoginId)).check(matches(withText("JakeWharton")))
    }

    @Test
    fun test_details_fragment_no_data() {

        val args = bundleOf("user" to "zzzsfdzfdsfzsfsdzfsd")

        launchFragmentInHiltContainer<UserDetailsFragment>(
            themeResId = R.style.Theme_GitHubApp,
            fragmentArgs = args
        )

        onView(withId(R.id.textViewError)).perform(waitUntilVisibleAction())

        onView(withId(R.id.textViewError)).check(matches(isDisplayed()))
    }
}