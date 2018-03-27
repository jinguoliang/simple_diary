package com.empty.jinux.simplediary.weather

import org.junit.Before
import org.junit.Test

/**
 * Created by jingu on 2018/3/8.
 */
class WeatherManagerRetrofitImplTest {
    private lateinit var mWeatherManager: WeatherManagerRetrofitImpl

    @Before
    fun setup(): Unit {
        mWeatherManager = WeatherManagerRetrofitImpl()
    }

    @Test
    fun getCurrentWeather() {
        mWeatherManager.getCurrentWeather(2.2,2.2) {
        }
    }

    @Test
    fun getWeatherIcon() {
    }

}