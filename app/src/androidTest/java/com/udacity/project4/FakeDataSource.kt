package com.udacity.project4

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource

class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    DONE: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        tasks?.let { return Result.Success(it) }
        return Result.Error("No reminders found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        tasks?.firstOrNull { it.id == id }?.let { return Result.Success(it) }
        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        tasks = mutableListOf()
    }


}