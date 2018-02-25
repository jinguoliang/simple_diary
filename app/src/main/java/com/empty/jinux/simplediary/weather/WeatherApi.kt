package com.empty.jinux.simplediary.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by jingu on 2018/2/25.
 */

interface WeatherApi {

    @GET("weather")
    fun getCurrentWeatherByCoordinates(@Query("lat") lat: Double,
                                       @Query("lon") lon: Double,
                                       @Query("APPID") id: String = "10e9cc635d4644ad7adf973b677af153"): Call<CurrentWeatherResult>
}