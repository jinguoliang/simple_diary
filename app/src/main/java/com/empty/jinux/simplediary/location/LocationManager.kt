package com.empty.jinux.simplediary.location

/**
 * Created by jingu on 2018/2/23.
 */
interface LocationManager {
    /**
     * get the last location
     */
    fun getLastLocation(callback: (Location) -> Unit)

    /**
     * refresh the location
     */
    fun refreshLocation(callback: (Boolean) -> Unit)

    /**
     * get the current address for the location
     */
    fun getCurrentAddress(callback: (String) -> Unit): Unit
}

