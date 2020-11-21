package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment

import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment() , OnMapReadyCallback{

    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    lateinit var Poi : PointOfInterest
    var lat : Double = 0.0
    var long : Double = 0.0
    var title = ""

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        DONE: add the map setup implementation
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        DONE: call this function after the user confirms on the selected location
        binding.saveLocation.setOnClickListener{
            onLocationSelected()
            view?.findNavController()?.navigate(R.id.action_selectLocationFragment_to_saveReminderFragment)
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //      DONE: put a marker to location that the user selected

        setPoiClick(map)
        //        DONE: add style to the map
        setMapStyle(map)
        enableMyLocation()
    }
    private fun onLocationSelected() {
        //        DONE: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
            _viewModel.latitude.value = lat
            _viewModel.longitude.value = long
            _viewModel.selectedPOI.value = Poi
        _viewModel.reminderSelectedLocationStr.value = title
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // DONE: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                    MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)
            )
            val zoomLevel = 15f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi.latLng, zoomLevel))
            poiMarker.showInfoWindow()
            Poi = poi
            lat= poi.latLng.latitude
            long = poi.latLng.longitude
            title = poi.name
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.map_style
                    )
            )

            if (!success) {
                Toast.makeText(context,"Style parsing failed.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(context, "error $e",Toast.LENGTH_LONG).show()
        }
    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true

        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
        //        DONE: zoom to the user location after taking his permission


        map.moveCamera(CameraUpdateFactory.zoomIn())
    }

}
