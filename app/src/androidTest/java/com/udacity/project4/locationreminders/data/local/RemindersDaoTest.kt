package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    DONE: Add testing implementation to the RemindersDao.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble())
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminderByIdNotFound() = runBlockingTest {
        // GIVEN - a random reminder id
        val reminderId = UUID.randomUUID().toString()
        // WHEN - Get the reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminderId)
        // THEN - The loaded data should be  null.
        assertNull(loaded)
    }

    @Test
    fun insertRemindersAndGetReminders() = runBlockingTest {
        // GIVEN - insert a reminder
        val reminder = ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble())

        database.reminderDao().saveReminder(reminder)

        // WHEN - Get reminders from the database
        val reminders = database.reminderDao().getReminders()

        // THEN - There is only 1 reminder in the database, and contains the expected values
        assertThat(reminders.size, `is`(1))
        assertThat(reminders[0].id, `is`(reminder.id))
        assertThat(reminders[0].title, `is`(reminder.title))
        assertThat(reminders[0].description, `is`(reminder.description))
        assertThat(reminders[0].location, `is`(reminder.location))
        assertThat(reminders[0].latitude, `is`(reminder.latitude))
        assertThat(reminders[0].longitude, `is`(reminder.longitude))
    }



    @Test
    fun deleteTasksAndGettingTasks() = runBlockingTest {
        // Given a reminder inserted
        val remindersList = listOf<ReminderDTO>(ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
                ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
                ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
                ReminderDTO("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()))

                remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        // When deleting all reminders
        database.reminderDao().deleteAllReminders()

        // THEN - The list is empty
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.isEmpty(), `is`(true))
    }

}