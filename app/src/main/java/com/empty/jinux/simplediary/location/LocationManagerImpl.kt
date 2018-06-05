package com.empty.jinux.simplediary.location

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.empty.jinux.baselibaray.logd
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.common.collect.Lists
import java.io.IOException
import java.util.*


/**
 * Created by jingu on 2018/2/23.
 *
 * Location Manager Implement with gms
 */
open class LocationManagerImpl constructor(val context: Activity) : LocationManager {
    private val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun getLastLocation(callback: (Location) -> Unit) {
        refreshLocation {
            getLastLocation(true, callback::invoke)
        }
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
                mFusedLocationClient.removeLocationUpdates(this)
            }
        }
        mFusedLocationClient.requestLocationUpdates(createLocationRequest(),
                mLocationCallback,
                Looper.getMainLooper() /* Looper */)
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    override fun getCurrentAddress(callback: (address: String) -> Unit): Unit {
        getLastLocation { location ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = try {
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            } catch (e: IOException) {
                Lists.newArrayList<Address>()
            }

            if (addresses.isNotEmpty()) {
                logd(addresses)
                val address = addresses[0]
                val addressStr =
                        if (Locale.getDefault() == Locale.CHINESE) (0..2).mapNotNull { address.getAddressLine(it) }.joinToString(" ")
                        else (2 downTo 0).mapNotNull { address.getAddressLine(it) }.joinToString(", ")
                callback(addressStr)
            }
        }
    }
}