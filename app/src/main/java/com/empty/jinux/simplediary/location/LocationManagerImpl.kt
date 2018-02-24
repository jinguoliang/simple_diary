package com.empty.jinux.simplediary.location

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import javax.inject.Inject


/**
 * Created by jingu on 2018/2/23.
 *
 * Location Manager Implement with gms
 */
class LocationManagerImpl constructor(val context: Activity) : LocationManager {
    val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    @SuppressLint("MissingPermission")
    override fun getLastLocation(callback: (Location) -> Unit) {
        mFusedLocationClient.lastLocation
                .addOnSuccessListener(context, { location: android.location.Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        callback(Location(location.latitude, location.longitude))
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

    protected fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    override fun getCurrentAddress(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}