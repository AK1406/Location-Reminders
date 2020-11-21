package com.udacity.project4.locationreminders.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R

class GeofenceHelper(base: Context?) :
        ContextWrapper(base) {
    var pendingIntent: PendingIntent? = null
    fun getGeofencingRequest(geofence: Geofence?): GeofencingRequest {
        return GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build()
    }

    fun getGeofence(ID: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
    }

    fun getGeofencePendingIntent(): PendingIntent? {
        if (pendingIntent != null) {
            return pendingIntent
        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        pendingIntent =
                PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    fun getErrorString(e: Exception): String {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return getString(
                        R.string.geofence_not_available)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return getString(
                        R.string.geofence_too_many_geofences)
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return getString(
                        R.string.geofence_too_many_pending_intents)
            }
        }
        return e.localizedMessage
    }

    companion object {
        private const val TAG = "GeofenceUtil"
    }
}