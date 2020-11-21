package com.udacity.project4.locationreminders.geofence


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.geofence.GeofenceTransitionsJobIntentService.Companion.enqueueWork

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        //DONE: implement the onReceive method to receive the geofencing events at the background
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        if (geofenceEvent.hasError()) {
            Log.d(TAG, "Error on receive !")
            return
        }

        when (geofenceEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                enqueueWork(context, intent)
            }
        }

    }
}