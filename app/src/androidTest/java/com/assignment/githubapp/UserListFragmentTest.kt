package com.assignment.githubapp

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.assignment.githubapp.util.SearchViewActionExtension
import com.assignment.githubapp.util.launchFragmentInHiltContainer
import com.assignment.githubapp.util.waitUntilVisibleAction
import com.assignment.githubapp.view.MainActivity
import com.assignment.githubapp.view.UserListFragment
import com.assignment.githubapp.view.adapter.UserListAdapter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock


/**
 * Test class which contains the test cases for user search and recyclerview UI actions
 *
 */

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockNavController = mock(NavController::class.java)

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        hiltRule.inject()

        launchFragmentInHiltContainer<UserListFragment> {
            mockNavController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), mockNavController)
        }
    }

    @Test
    fun test_searchDataSuccess() {

        onView(withId(R.id.searchView)).perform(waitUntilVisibleAction())

        onView(withId(R.id.searchView)).perform(SearchViewActionExtension.submitText("jake"))

        onView(withId(R.id.recyclerViewUsers)).perform(waitUntilVisibleAction())

        onView(withId(R.id.textViewError)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        onView(withId(R.id.recyclerViewUsers)).perform(waitUntilVisibleAction())

        onView(withId(R.id.recyclerViewUsers)).check(matches(isDisplayed()))
    }

    @Test
    fun test_searchDataNotFound() {

        onView(withId(R.id.searchView)).perform(waitUntilVisibleAction())

        onView(withId(R.id.searchView)).perform(SearchViewActionExtension.submitText("zzzsfdzfdsfzsfsdzfsd"))

        onView(withId(R.id.textViewEmptyResults)).perform(waitUntilVisibleAction())

        onView(withId(R.id.textViewEmptyResults)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigateToDetailFragment(){

        onView(withId(R.id.searchView)).perform(waitUntilVisibleAction())

        onView(withId(R.id.searchView)).perform(SearchViewActionExtension.submitText("jake"))

        onView(withId(R.id.recyclerViewUsers)).perform(waitUntilVisibleAction())

        onView(withId(R.id.recyclerViewUsers)).perform(RecyclerViewActions.actionOnItemAtPosition<UserListAdapter.UserViewHolder>(1, click()))

        mockNavController.currentDestination?.id?.let { assert(it == R.id.details_fragment) }

    }
}