package com.empty.jinux.simplediary.location

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.*


/**
 * Created by jingu on 2018/2/23.
 *
 * Location Manager Implement with gms
 */
open class LocationManagerImpl constructor(val context: Activity) : LocationManager {
    private val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    override fun getLastLocation(callback: (Location) -> Unit) {
        getLastLocation(true, callback)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(tryAgain: Boolean, callback: (Location) -> Unit) {
        mFusedLocationClient.lastLocation
                .addOnSuccessListener(context, { location: android.location.Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        callback(Location(location.latitude, location.longitude))
                    } else {
                        refreshLocation {
                            getLastLocation(false, callback)
                        }
                    }
                })
    }



    @SuppressLint("MissingPermission")
    override fun refreshLocation(callback: (suc: Boolean) -> Unit) {
        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
        mFusedLocationClient.requestLocationUpdates(createLocationRequest(),
                mLocationCallback,
                null /* Looper */);
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    override fun getCurrentAddress(callback: (address: String) -> Unit): Unit {
        getLastLocation { location ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressStr = "${address.getAddressLine(0)}${address.getAddressLine(1)}${address.featureName}"
                callback(addressStr)
            }
        }
    }
}