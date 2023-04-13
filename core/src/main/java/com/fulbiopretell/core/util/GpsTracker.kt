package com.fulbiopretell.retoyape.core.util

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat

class GpsTracker : Service , LocationListener{

    interface SettingInterface {
        fun requestPermissionLocation()
        fun openSetting()
    }

    interface ChangedLocation {
        fun changed(location: Location)
    }

    private val context: Context
    var location: Location? = null
    var isGpsActive = false
    var networkActive = false
    var permissionGPS = false
    var isInitiated = false
    var locationManager: LocationManager? = null
    var settingInterface: SettingInterface? = null
    var changedLocation: ChangedLocation? = null
    var openRequest = false

    constructor() {
        context = this.applicationContext
    }

    constructor(context: Context, settingInterface: SettingInterface?) : super() {
        this.context = context
        this.settingInterface = settingInterface
        init()
    }

    constructor(context: Context, settingInterface: SettingInterface?,changedLocation: ChangedLocation) : super() {
        this.context = context
        this.settingInterface = settingInterface
        this.changedLocation = changedLocation
        init()
    }

    private fun init() {
        currentLocation
        locationUpdates(MIN_TIME_UPDATES_MS)
        isInitiated = permissionGPS
    }

    val currentLocation: Unit
        get() {
            try {
                locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
                isGpsActive = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                networkActive = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (e: Exception) {
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                settingInterface!!.requestPermissionLocation()
                openRequest = true
                permissionGPS = false
            } else {
                openRequest = false
                permissionGPS = true
                val locationNET = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val locationGPS = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location = getBetterLocation(locationNET, locationGPS)
            }
        }

    fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (!openRequest) {
                settingInterface!!.requestPermissionLocation()
                openRequest = true
            }
            permissionGPS = false
        } else {
            openRequest = false
            permissionGPS = true
            if (isInitiated) init()
        }
        return permissionGPS
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        this.location = getBetterLocation(location, this.location)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {
        isGpsActive = true
    }

    override fun onProviderDisabled(provider: String) {
        isGpsActive = false
    }

    val longitude: Double
        get() = if (location != null) location!!.longitude else 0.0
    val latitude: Double
        get() = if (location != null) location!!.latitude else 0.0

    fun onStop() {
        locationManager!!.removeUpdates(this)
    }

    /**
     * Determines whether one Location reading is better than the current Location fix.
     * Code taken from
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     *
     * @param newLocation         The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new
     * one
     * @return The better Location object based on recency and accuracy.
     */
    protected fun getBetterLocation(newLocation: Location?, currentBestLocation: Location?): Location? {
        // Check for nulls
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation
        }
        if (newLocation == null) {
            // An old location is always better than no location
            return currentBestLocation
        }

        // Check whether the new location is newer or older
        val timeDelta = newLocation.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TIME_THRESHHOLD_MS
        val isSignificantlyOlder = timeDelta < -TIME_THRESHHOLD_MS
        val isNewer = timeDelta > 0

        // If it's been more than THRESHHOLD minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (newLocation.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            newLocation.provider,
            currentBestLocation.provider
        )

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation
        } else if (isNewer && !isLessAccurate) {
            return newLocation
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation
        }
        return currentBestLocation
    }

    /**
     * Checks whether two providers are the same
     */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    fun checkGpsProvider() {
        isGpsActive = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun locationUpdates(timeUpdates: Int) {
        locationManager!!.removeUpdates(this)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (!openRequest) {
                settingInterface!!.requestPermissionLocation()
                openRequest = true
            }
            permissionGPS = false
        } else {
            openRequest = false
            if (locationManager!!.allProviders.contains(LocationManager.NETWORK_PROVIDER)) locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                timeUpdates.toLong(), 0f, this
            )
            if (locationManager!!.allProviders.contains(LocationManager.GPS_PROVIDER)) locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                timeUpdates.toLong(), 0f, this
            )
        }
    }



    companion object {
        private const val TIME_THRESHHOLD_MS = 60 * 1 * 1000 // 60 secs
        private const val MIN_TIME_UPDATES_MS = 5 * 1000 // 5 secs
        const val requestCode = 2222
    }
}