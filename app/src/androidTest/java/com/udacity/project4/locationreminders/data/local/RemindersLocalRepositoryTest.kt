package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.FakeReminderDao
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    DONE: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    val list =  listOf<ReminderDTO>(ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
            ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
            ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
            ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()))

    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]

    private val newReminder = list[3]

    private lateinit var fakeRemindersDao: FakeReminderDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setup() {
        fakeRemindersDao = FakeReminderDao()
        remindersLocalRepository = RemindersLocalRepository(
                fakeRemindersDao, Dispatchers.Unconfined
        )
    }

    @Test
    fun savesToLocalCache() = runBlockingTest {
        var list = mutableListOf<ReminderDTO>()
        list.addAll(fakeRemindersDao.remindersServiceData.values)
        // Make sure newReminder is not in the local datasources or cache
        assertThat(list).doesNotContain(newReminder)
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).doesNotContain(
                newReminder
        )

        // When a reminder is saved to the tasks repository
        remindersLocalRepository.saveReminder(newReminder)

        list = mutableListOf()
        list.addAll(fakeRemindersDao.remindersServiceData.values)
        // Then the local sources are called and the cache is updated
        assertThat(list).contains(newReminder)

        val result = remindersLocalRepository.getReminders() as? Result.Success
        assertThat(result?.data).contains(newReminder)
    }

    @Test
    fun getReminderByIdThatExistsInLocalCache() = runBlockingTest {
        // Make sure newReminder is not in the local cache
        assertThat((remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message).isEqualTo(
                "Reminder not found!")

        fakeRemindersDao.remindersServiceData[reminder1.id] = reminder1

        // When reminder is fetch by id
        val loadedReminder = (remindersLocalRepository.getReminder(reminder1.id) as? Result.Success)?.data

        Assert.assertThat<ReminderDTO>(loadedReminder as ReminderDTO, CoreMatchers.notNullValue())
        Assert.assertThat(loadedReminder.id, CoreMatchers.`is`(reminder1.id))
        Assert.assertThat(loadedReminder.title, CoreMatchers.`is`(reminder1.title))
        Assert.assertThat(loadedReminder.description, CoreMatchers.`is`(reminder1.description))
        Assert.assertThat(loadedReminder.location, CoreMatchers.`is`(reminder1.location))
        Assert.assertThat(loadedReminder.latitude, CoreMatchers.`is`(reminder1.latitude))
        Assert.assertThat(loadedReminder.longitude, CoreMatchers.`is`(reminder1.longitude))
    }

    @Test
    fun getReminderByIdThatDoesNotExistInLocalCache() = runBlockingTest {

        val message = (remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message
        Assert.assertThat<String>(message, CoreMatchers.notNullValue())
        assertThat(message).isEqualTo("Reminder does not exist !")

    }

    @Test
    fun deleteAllReminders_EmptyListFetchedFromLocalCache() = runBlockingTest {
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()

        fakeRemindersDao.remindersServiceData[reminder1.id] = reminder1
        fakeRemindersDao.remindersServiceData[reminder2.id] = reminder2
        fakeRemindersDao.remindersServiceData[reminder3.id] = reminder3

        // When - reminders are fetched from  repository - should not be empty
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isNotEmpty()

        remindersLocalRepository.deleteAllReminders()

        // Then - fetching should return empty list
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()
    }
}