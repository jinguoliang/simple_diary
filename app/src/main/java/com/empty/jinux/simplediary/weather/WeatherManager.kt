package com.empty.jinux.simplediary.weather

/**
 * Created by jingu on 2018/2/25.
 */
interface WeatherManager {
    fun getCurrentWeather(lat: Double, lon: Double, callback: (Weather) -> Unit): Unit

}