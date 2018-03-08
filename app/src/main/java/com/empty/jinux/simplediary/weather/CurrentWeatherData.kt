package com.empty.jinux.simplediary.weather

/**
 * Created by jingu on 2018/2/25.
 */
data class CurrentWeatherResult(val weather: List<Weather>)

data class Weather(val id: Long,
                   val main: String,
                   val description: String,
                   val icon: String) {
    fun iconUri(): String {
        return "http://openweathermap.org/img/w/$icon.png"
    }
}