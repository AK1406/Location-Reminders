package com.udacity.project4.locationreminders.savereminder

import androidx.test.ext.junit.runners.AndroidJUnit4

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import com.udacity.project4.locationreminders.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //DONE: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    val list = listOf<ReminderDataItem>(ReminderDataItem("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()))
    private val firstReminder = list[0]

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel


    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(firstReminder)
        Assert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
    }

    @Test
    fun returnError() {
        fakeDataSource = FakeDataSource(null)
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        firstReminder.title = null
        saveReminderViewModel.validateAndSaveReminder(firstReminder)
        Assert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.`is`(R.string.err_enter_title))
    }

}