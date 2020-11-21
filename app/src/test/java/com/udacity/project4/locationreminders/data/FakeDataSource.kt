package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource

class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    DONE: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        TODO("Return the reminders")
        tasks?.let { return Result.Success(it) }
        return Result.Error("No reminders found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("save the reminder")
        tasks?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
        tasks?.firstOrNull { it.id == id }?.let { return Result.Success(it) }
        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
        tasks = mutableListOf()
    }


}