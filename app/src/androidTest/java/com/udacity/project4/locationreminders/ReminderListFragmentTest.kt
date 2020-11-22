package com.udacity.project4.locationreminders

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.FakeDataSource
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest :
        AutoCloseKoinTest() {

//    DONE: test the navigation of the fragments.
//    DONE: test the displayed data on the UI.
//    DONE: add testing for the error messages.

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun setup() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel =
                RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        stopKoin()

        val myModule = module {
            single {
                reminderListViewModel
            }
        }
        // new koin module
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun displayRemindersList() = runBlockingTest {

        val list = listOf<ReminderDTO>(
                ReminderDTO("title", "description", "location", (-360..360).random().toDouble(),(-360..360).random().toDouble()),
                ReminderDTO(
                        "title",
                        "description",
                        "location",
                        (-360..360).random().toDouble(),
                        (-360..360).random().toDouble()
                ),
                ReminderDTO(
                        "title",
                        "description",
                        "location",
                        (-360..360).random().toDouble(),
                        (-360..360).random().toDouble()
                ),
                ReminderDTO(
                        "title",
                        "description",
                        "location",
                        (-360..360).random().toDouble(),
                        (-360..360).random().toDouble()
                )
        )

        list.forEach {
            fakeDataSource.saveReminder(it)
        }

        // GIVEN -
        val reminders = (fakeDataSource.getReminders() as? Result.Success)?.data

        val firstItem = reminders!![0]

        onView(
                Matchers.allOf(
                        withText(firstItem.location),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.reminderCardView),
                                        0
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
                .check(matches(withText(firstItem.location)))
    }

    @Test
    fun navigateToAddReminder() = runBlockingTest {
        // WHEN - Details fragment launched to display task
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun errorSnackBackShown() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        // WHEN - Details fragment launched to display task
        onView(withText("No reminders found"))
                .check(matches(isDisplayed()))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}