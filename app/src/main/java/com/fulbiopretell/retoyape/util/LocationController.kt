package com.fulbiopretell.retoyape.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class LocationController(context: Context?, intervalTime: Int, locationManagerListener: LocationControllerListener?) {
    private val context: Context
    private var googleApiClient: GoogleApiClient? = null
    private var lastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    private var settingsApiBuilder: LocationSettingsRequest.Builder? = null
    private val locationManagerListener: LocationControllerListener
    private val intervalTime: Int

    init {
        requireNotNull(context) { "Context can't be Null" }
        requireNotNull(locationManagerListener) { "LocationManagerListener can't be Null" }
        this.intervalTime = intervalTime
        this.context = context
        this.locationManagerListener = locationManagerListener
    }

    fun connect() {
        validateLocationPermission()
    }

    fun disconnect() {
        stopLocationUpdates()
        if (googleApiClient != null) {
            googleApiClient!!.disconnect()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) settingGoogleLocationAPI() else locationManagerListener.onLocationPermissionDenied()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> startLocationUpdates()
                Activity.RESULT_CANCELED -> locationManagerListener.onGpsUnavailable()
            }
        }
    }

    fun checkGspAvailability() {
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(settingsApiBuilder!!.build())
        task.addOnSuccessListener((context as Activity)) { locationSettingsResponse: LocationSettingsResponse? -> startLocationUpdates() }
        task.addOnFailureListener(context) { e: Exception ->
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED -> try {
                    val resolvable = e as ResolvableApiException
                    resolvable.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: SendIntentException) {
                    sendEx.printStackTrace()
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> showActiveGPSDialog()
            }
        }
    }

    private fun validateLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                settingGoogleLocationAPI()
            } else {
                askLocationPermission()
            }
        } else {
            settingGoogleLocationAPI()
        }
    }

    private fun askLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        /*String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                "android.permission.ACCESS_BACKGROUND_LOCATION"};*/ActivityCompat.requestPermissions((context as Activity), permissions, LOCATION_PERMISSION_REQUEST)
    }

    private fun settingGoogleLocationAPI() {
        googleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                @Throws(SecurityException::class)
                override fun onConnected(bundle: Bundle?) {
                    createLocationRequest()
                }

                override fun onConnectionSuspended(i: Int) {
                    locationManagerListener.onError(LocationManagerError.GOOGLE_API_CONNECTION_SUSPENDED)
                }
            })
            .addOnConnectionFailedListener { connectionResult: ConnectionResult? -> locationManagerListener.onError(LocationManagerError.GOOGLE_API_CONNECTION_FAILED) }
            .addApi(LocationServices.API)
            .build()
        googleApiClient?.connect()
    }

    private val myLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (result.lastLocation == null) return
            if (lastLocation == null) {
                locationManagerListener!!.onGetLocationCompleted(result.lastLocation)
            }
            lastLocation = result.lastLocation
            locationManagerListener!!.onLocationUpdate(lastLocation)
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
            if (!locationAvailability.isLocationAvailable) {
                stopLocationUpdates()
                locationManagerListener!!.onGpsUnavailable()
            }
        }
    }

    @Throws(SecurityException::class)
    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = intervalTime.toLong()
        locationRequest!!.fastestInterval = (intervalTime / 2).toLong()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.maxWaitTime = (intervalTime * 2).toLong()
        settingsApiBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        checkGspAvailability()
    }

    @SuppressLint("MissingPermission")
    @Throws(SecurityException::class)
    private fun startLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { location: Location? ->
                lastLocation = location
                if (lastLocation != null) {
                    locationManagerListener.onGetLocationCompleted(lastLocation)
                }
                if (googleApiClient != null) LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest!!, myLocationCallback, null)
            }
            .addOnFailureListener { e: Exception? -> if (googleApiClient != null) LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest!!, myLocationCallback, null) }
    }

    @Throws(SecurityException::class)
    private fun stopLocationUpdates() {
        if (googleApiClient != null) LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(myLocationCallback)
    }

    private fun showActiveGPSDialog() {
        if (AppUtils.isConnected(context)) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context.resources.getString(com.fulbiopretell.retoyape.R.string.discounts_gps_unavailable_message))
                .setTitle(context.resources.getString(com.fulbiopretell.retoyape.R.string.app_name))
            builder.setPositiveButton("Activar") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                locationManagerListener.onGpsUnavailable()
            }
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        } else {
            Toast.makeText(context, context.getString(com.fulbiopretell.retoyape.R.string.exception_airplane_mode_off), Toast.LENGTH_LONG).show()
        }
    }

    enum class LocationManagerError {
        GOOGLE_API_CONNECTION_FAILED, GOOGLE_API_CONNECTION_SUSPENDED
    }

    interface LocationControllerListener {
        fun onLocationPermissionDenied()
        fun onGpsUnavailable()
        fun onGetLocationCompleted(location: Location?)
        fun onLocationUpdate(location: Location?)
        fun onError(error: LocationManagerError?)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1000
        private const val REQUEST_CHECK_SETTINGS = 0x1
    }
}