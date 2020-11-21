package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceHelper
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient
    private val GEOFENCE_RADIUS = 500f
    private lateinit var reminderData: ReminderDataItem
    private lateinit var geofenceHelper: GeofenceHelper

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        geofencingClient = LocationServices.getGeofencingClient(context!!)
        geofenceHelper = GeofenceHelper(context)

        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                    NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val geofenceId = UUID.randomUUID().toString()

//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
            if (latitude != null && longitude != null && !TextUtils.isEmpty(title))
                addGeofence(LatLng(latitude, longitude), GEOFENCE_RADIUS, geofenceId)

            _viewModel.validateAndSaveReminder(ReminderDataItem(title,description,location, latitude,longitude))

            view.findNavController().navigate(R.id.action_saveReminderFragment_to_reminderListFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()

    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(
            latLng: LatLng,
            radius: Float,
            geofenceId: String) {
        val geofence: Geofence = geofenceHelper.getGeofence(
                geofenceId,
                latLng,
                radius,
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = geofenceHelper.getGeofencePendingIntent()
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(OnSuccessListener<Void?> {
                    // Toast.makeText(context,"geofence added",Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Geofence Added")
                })
                .addOnFailureListener(OnFailureListener { e ->
                    val errorMessage: String = geofenceHelper.getErrorString(e)
                    Toast.makeText(context, "Please give background location permission", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "fail in creating geofence: $errorMessage")
                })
    }

}
private const val TAG = "SaveReminderFragment"