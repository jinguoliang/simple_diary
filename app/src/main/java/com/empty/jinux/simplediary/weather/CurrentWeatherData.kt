package com.empty.jinux.simplediary.weather

import com.google.gson.annotations.SerializedName

/**
 * Created by jingu on 2018/2/25.
 */
data class CurrentWeatherResult(@SerializedName("weather") val weather: List<Weather>)

data class Weather(@SerializedName("id") val id: Long,
                   @SerializedName("main") val main: String,
                   @SerializedName("description") val description: String,
                   @SerializedName("icon") val icon: String)