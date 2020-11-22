package com.udacity.project4.locationreminders.locationreminders

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    //DONE: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    val list = listOf<ReminderDTO>(
            ReminderDTO("title", "description", "location", 0.0, 0.0),
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
    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getRemindersList() {
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        reminderListViewModel.loadReminders()
        assertThat( reminderListViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
        assertThat( reminderListViewModel.remindersList.getOrAwaitValue().size, `is`(remindersList.size))
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource(mutableListOf())
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun returnError() {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), `is`("No reminders found"))
    }

}