package com.udacity.project4

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDao


class FakeReminderDao : RemindersDao {

    var shouldReturnError = false

     public val remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    override suspend fun getReminders(): List<ReminderDTO> {
        if (shouldReturnError) {
            throw (Exception("Test exception"))
        }

        val list = mutableListOf<ReminderDTO>()
        list.addAll(remindersServiceData.values)
        return list
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        remindersServiceData[reminderId]?.let {
            return it
        }
        return null
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }

}